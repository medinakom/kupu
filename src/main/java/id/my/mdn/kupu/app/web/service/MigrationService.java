/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.dao.AudioSegmentFacade;
import id.my.mdn.kupu.app.web.dao.VideoSegmentFacade;
import id.my.mdn.kupu.app.web.model.AudioSegment;
import id.my.mdn.kupu.app.web.model.AudioSegmentId;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.model.VideoSegment;
import id.my.mdn.kupu.app.web.model.VideoSegmentId;
import jakarta.annotation.Resource;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aphasan
 */
@Stateless
public class MigrationService {

    @Resource
    private SessionContext ctx;

    @Inject
    private VideoSegmentFacade videoSegmentFacade;

    @Inject
    private AudioSegmentFacade audioSegmentFacade;

    @Inject
    private MediaService mediaService;

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    private static final AtomicBoolean isMigrating = new AtomicBoolean(false);

    private final AtomicLong videoSuccess = new AtomicLong(0);
    private final AtomicLong videoFailed = new AtomicLong(0);
    private final AtomicLong audioSuccess = new AtomicLong(0);
    private final AtomicLong audioFailed = new AtomicLong(0);
    private LocalDateTime startTime;

    @Asynchronous
    public void migrateAll() {
        if (!isMigrating.compareAndSet(false, true)) {
            Logger.getLogger(MigrationService.class.getName()).log(Level.WARNING,
                    "Migration already in progress. Skipping.");
            return;
        }

        try {
            startTime = LocalDateTime.now();
            videoSuccess.set(0);
            videoFailed.set(0);
            audioSuccess.set(0);
            audioFailed.set(0);

            Logger.getLogger(MigrationService.class.getName()).log(Level.INFO, "Migration process started...");

            long totalKajian = em.createQuery("SELECT COUNT(vk) FROM VideoKajian vk", Long.class).getSingleResult();
            Logger.getLogger(MigrationService.class.getName()).log(Level.INFO,
                    "Total VideoKajian records to check: {0}", totalKajian);

            int batchSize = 50;
            for (int i = 0; i < totalKajian; i += batchSize) {
                List<VideoKajian> batch = em.createQuery("SELECT vk FROM VideoKajian vk", VideoKajian.class)
                        .setFirstResult(i)
                        .setMaxResults(batchSize)
                        .getResultList();

                Logger.getLogger(MigrationService.class.getName()).log(Level.INFO,
                        "Processing VideoKajian batch {0} to {1}...",
                        new Object[] { i, Math.min(i + batchSize, totalKajian) });

                for (VideoKajian vk : batch) {
                    migrateVideoKajian(vk);
                }
            }

            Logger.getLogger(MigrationService.class.getName()).log(Level.INFO, "Migration process finished.");
            writeMigrationReport();

        } catch (Exception ex) {
            Logger.getLogger(MigrationService.class.getName()).log(Level.SEVERE, "Migration task failed unexpectedly!",
                    ex);
        } finally {
            isMigrating.set(false);
        }
    }

    private void writeMigrationReport() {
        try {
            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, endTime);

            StringBuilder sb = new StringBuilder();
            sb.append("Migration Report\n");
            sb.append("================\n");
            sb.append("Started: ").append(startTime).append("\n");
            sb.append("Finished: ").append(endTime).append("\n");
            sb.append("Duration: ").append(duration.toMinutes()).append(" minutes ").append(duration.toSecondsPart())
                    .append(" seconds\n\n");
            sb.append("Video Segments:\n");
            sb.append("  - Success: ").append(videoSuccess.get()).append("\n");
            sb.append("  - Failed:  ").append(videoFailed.get()).append("\n\n");
            sb.append("Audio Segments:\n");
            sb.append("  - Success: ").append(audioSuccess.get()).append("\n");
            sb.append("  - Failed:  ").append(audioFailed.get()).append("\n");

            Path reportPath = mediaService.getMediaDir(null, MediaService.VIDEO).getParent()
                    .resolve("migration_report.txt");
            Files.writeString(reportPath, sb.toString(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            Logger.getLogger(MigrationService.class.getName()).log(Level.INFO, "Migration report written to: {0}",
                    reportPath);
        } catch (Exception ex) {
            Logger.getLogger(MigrationService.class.getName()).log(Level.SEVERE, "Failed to write migration report",
                    ex);
        }
    }

    public void migrateVideoKajian(VideoKajian vk) {
        // Migrate Video Segments in batches of 50
        List<String> videoSegments;
        MigrationService self = ctx.getBusinessObject(MigrationService.class);

        int batchSize = 50;
        do {
            videoSegments = em.createQuery(
                    "SELECT v.name FROM VideoSegment v WHERE v.video = :video AND v.filePath IS NULL", String.class)
                    .setParameter("video", vk)
                    .setMaxResults(batchSize) // Process in chunks
                    .getResultList();

            if (!videoSegments.isEmpty()) {
                self.migrateVideoSegmentsBatch(vk.getId(), videoSegments);
                Logger.getLogger(MigrationService.class.getName()).log(Level.INFO,
                        "Migrated {0} video segments for {1}...", new Object[] { videoSegments.size(), vk.getId() });
            }
        } while (videoSegments.size() == batchSize);

        // Migrate Audio Segments in batches of 50
        List<String> audioSegments;
        do {
            audioSegments = em.createQuery(
                    "SELECT a.name FROM AudioSegment a WHERE a.video = :video AND a.filePath IS NULL", String.class)
                    .setParameter("video", vk)
                    .setMaxResults(batchSize)
                    .getResultList();

            if (!audioSegments.isEmpty()) {
                self.migrateAudioSegmentsBatch(vk.getId(), audioSegments);
                Logger.getLogger(MigrationService.class.getName()).log(Level.INFO,
                        "Migrated {0} audio segments for {1}...", new Object[] { audioSegments.size(), vk.getId() });
            }
        } while (audioSegments.size() == batchSize);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void migrateVideoSegmentsBatch(String videoId, List<String> segmentNames) {
        for (String name : segmentNames) {
            try {
                VideoSegment segment = videoSegmentFacade.find(new VideoSegmentId(videoId, name));
                if (segment != null && segment.getContent() != null) {
                    Path storageDir = mediaService.getStorageDir(segment.getVideo(), MediaService.VIDEO);
                    Path dest = storageDir.resolve(name);

                    if (!Files.exists(dest)) {
                        Files.write(dest, segment.getContent(), StandardOpenOption.CREATE);
                    }

                    segment.setFilePath(dest.toAbsolutePath().toString());
                    segment.setContent(null);
                    em.merge(segment);
                    videoSuccess.incrementAndGet();
                }
            } catch (IOException ex) {
                videoFailed.incrementAndGet();
                Logger.getLogger(MigrationService.class.getName()).log(Level.SEVERE,
                        "Failed to migrate video segment " + name, ex);
            }
        }
        em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void migrateAudioSegmentsBatch(String videoId, List<String> segmentNames) {
        for (String name : segmentNames) {
            try {
                AudioSegment segment = audioSegmentFacade.find(new AudioSegmentId(videoId, name));
                if (segment != null && segment.getContent() != null) {
                    Path storageDir = mediaService.getStorageDir(segment.getVideo(), MediaService.AUDIO);
                    Path dest = storageDir.resolve(name);

                    if (!Files.exists(dest)) {
                        Files.write(dest, segment.getContent(), StandardOpenOption.CREATE);
                    }

                    segment.setFilePath(dest.toAbsolutePath().toString());
                    segment.setContent(null);
                    em.merge(segment);
                    audioSuccess.incrementAndGet();
                }
            } catch (IOException ex) {
                audioFailed.incrementAndGet();
                Logger.getLogger(MigrationService.class.getName()).log(Level.SEVERE,
                        "Failed to migrate audio segment " + name, ex);
            }
        }
        em.flush();
    }
}
