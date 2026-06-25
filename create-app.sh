#!/bin/bash

# Kupu Application Generator
# Usage: ./create-app.sh <app-name> [base-package]
# Example: ./create-app.sh kupu-school id.my.mdn.kupu.school

if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    echo "Kupu Application Generator"
    echo "Usage: $(basename "$0") <app-name> [base-package]"
    echo ""
    echo "Options:"
    echo "  -h, --help      Display this help message"
    echo ""
    echo "Arguments:"
    echo "  app-name        Name of the new application (e.g. 'kupu-school'). A directory"
    echo "                  with this name will be created as a sibling of the kupu/ folder."
    echo "  base-package    Java base package for the application (optional)."
    echo "                  If omitted, it is auto-derived from app-name:"
    echo "                    kupu-school  →  id.my.mdn.kupu.school"
    echo "                    myapp        →  id.my.mdn.kupu.myapp"
    echo ""
    echo "Examples:"
    echo "  $(basename "$0") kupu-school"
    echo "  $(basename "$0") kupu-school id.my.mdn.kupu.school"
    echo "  $(basename "$0") myapp net.example.myapp"
    exit 0
fi

APP_NAME=$1
NEW_PACKAGE=$2

if [ -z "$APP_NAME" ]; then
    echo "Usage: $(basename "$0") <app-name> [base-package]"
    echo "Run '$(basename "$0") --help' for details and examples."
    exit 1
fi

PROJECT_ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
TARGET_DIR="$(dirname "$PROJECT_ROOT")/$APP_NAME"
DEFAULT_PACKAGE="id.my.mdn.kupu.app"

if [ -z "$NEW_PACKAGE" ]; then
    SUFFIX=${APP_NAME#kupu-}
    if [ "$SUFFIX" != "$APP_NAME" ]; then
        NEW_PACKAGE="id.my.mdn.kupu.$SUFFIX"
    else
        NEW_PACKAGE="id.my.mdn.kupu.$APP_NAME"
    fi
fi

if [ -d "$TARGET_DIR" ]; then
    if [ "$(ls -A "$TARGET_DIR")" ]; then
        echo "❌ Error: Target directory $TARGET_DIR already exists and is not empty."
        echo "Please remove it or provide a different app name."
        exit 1
    else
        echo "Target directory $TARGET_DIR exists but is empty. Proceeding..."
    fi
fi

echo "🚀 Generating Kupu Application: $APP_NAME"
echo "📦 Base Package: $NEW_PACKAGE"
echo "📂 Target Directory: $TARGET_DIR"

# 1. Clean Copy
mkdir -p "$TARGET_DIR"
cp -r "$PROJECT_ROOT/kupu-starter/." "$TARGET_DIR/"
rm -rf "$TARGET_DIR/target"
rm -rf "$TARGET_DIR/.git"
rm -f  "$TARGET_DIR/nb-configuration.xml"
# css and locales are provided by kupu-core — remove them from the generated app
rm -rf "$TARGET_DIR/src/main/webapp/WEB-INF/resources/css"
rm -rf "$TARGET_DIR/src/main/webapp/WEB-INF/resources/locales"

# 2. Rename Directory Structure
OLD_PATH=$(echo $DEFAULT_PACKAGE | tr . /)
NEW_PATH=$(echo $NEW_PACKAGE | tr . /)

# Function to safely move package contents
move_package() {
    local base=$1
    local old_p=$2
    local new_p=$3

    if [ "$old_p" == "$new_p" ]; then
        return
    fi
    
    if [ -d "$base/$old_p" ]; then
        # Use temp dir to avoid issues if new path is a sub-path of old path
        local temp_dir=$(mktemp -d)
        cp -r "$base/$old_p/." "$temp_dir/"
        rm -rf "$base/$old_p"
        
        # Clean up old empty folders upward
        local current_dir=$(dirname "$base/$old_p")
        while [ "$current_dir" != "$base" ] && [ "$current_dir" != "/" ] && [ "$current_dir" != "." ]; do
            rmdir "$current_dir" 2>/dev/null || break
            current_dir=$(dirname "$current_dir")
        done
        
        mkdir -p "$base/$new_p"
        cp -r "$temp_dir/." "$base/$new_p/"
        rm -rf "$temp_dir"
    fi
}

move_package "$TARGET_DIR/src/main/java" "$OLD_PATH" "$NEW_PATH"
move_package "$TARGET_DIR/src/main/resources" "$OLD_PATH" "$NEW_PATH"
move_package "$TARGET_DIR/src/test/java" "$OLD_PATH" "$NEW_PATH"

# Ensure resource package directory exists even if it was empty in starter
mkdir -p "$TARGET_DIR/src/main/resources/$NEW_PATH"

# 3. Global String Replacement
find "$TARGET_DIR" -type f \( -name "*.java" -o -name "*.xml" -o -name "*.xhtml" -o -name "*.sh" -o -name "pom.xml" -o -name "*.properties" \) -exec sed -i "s/$DEFAULT_PACKAGE/$NEW_PACKAGE/g" {} +

# Update artifactId and name
sed -i "s/<artifactId>kupu-starter<\/artifactId>/<artifactId>$APP_NAME<\/artifactId>/g" "$TARGET_DIR/pom.xml"
sed -i "s/<name>kupu-starter<\/name>/<name>$APP_NAME<\/name>/g" "$TARGET_DIR/pom.xml"

# 4. Create config
echo "$NEW_PACKAGE" > "$TARGET_DIR/.generator-config"

# 5. Git Init
cd "$TARGET_DIR"
git init -q
git branch -m main
git add .
git commit -m "[init] chore: initialize $APP_NAME as a Kupu application" -q

# 6. VERIFY ARCHITECTURE
echo "🔎 Verifying architecture compliance..."

MVN_CMD=""
if command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
elif command -v maven &> /dev/null; then
    MVN_CMD="maven"
fi

if [ -n "$MVN_CMD" ]; then
    # We run it synchronously to provide feedback
    $MVN_CMD test -Dtest=ArchitectureTest > /tmp/arch_test_out 2>&1
    if [ $? -eq 0 ]; then
        echo "✅ Architecture verification PASSED!"
    else
        echo "⚠️  Architecture verification FAILED. Please review the new project."
        # If it failed, don't exit 1 so the user can still see the results
    fi
else
    echo "⏭️  Maven not found, skipping immediate verification."
fi

# 7. CLEAN UP
rm -rf "$TARGET_DIR/target"

echo "✨ Application $APP_NAME generated successfully!"
echo "👉 Get started: cd $TARGET_DIR && mvn clean install"
echo "🚀 Run with Payara Micro: mvn payara-micro:start"
