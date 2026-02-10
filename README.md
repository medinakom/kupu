# Kupu Development Workspace

This workspace is designed to separate framework development from application development.

## 📁 Projects

- **[kupu-core](kupu-core/)** (Framework): The standalone Kupu framework library.
- **[kupu-starter](kupu-starter/)** (Skeleton): The base application used to generate new Kupu apps.

## 🚀 Independent Development

Each project is its own independent Git repository.

1. **Core Team**: Works inside `kupu-core/`.
2. **Skeleton Maintainers**: Work inside `kupu-starter/`.
3. **App Developers**: Generate a new repo and work there.

## 🏗️ Generating a New Application

Create any application type (School, ERP, Logistics, etc.) instantly:

1. **Run the generator:**
   ```bash
   ./create-app.sh <app-name>
   # Example: ./create-app.sh kupu-school
   ```

2. **Access your new app:**
   The app is created as a sibling directory.
   ```bash
   cd ../kupu-school
   ```

## 📜 Framework Documentation

Found inside `kupu-core/`:
- [Knowledge Base](kupu-core/KNOWLEDGE_BASE.md)
- [Contributing Guidelines](kupu-core/CONTRIBUTING.md)
- [Improvement Roadmap](kupu-core/TODO.md)
