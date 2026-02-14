#!/bin/bash

# Kupu Application Generator
# Usage: ./create-app.sh <app-name> [base-package]
# Example: ./create-app.sh kupu-school id.my.mdn.kupu.school

APP_NAME=$1
NEW_PACKAGE=$2

if [ -z "$APP_NAME" ]; then
    echo "Usage: ./create-app.sh <app-name> [base-package]"
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
    echo "Cleaning up existing $TARGET_DIR..."
    rm -rf "$TARGET_DIR"
fi

echo "🚀 Generating Kupu Application: $APP_NAME"
echo "📦 Base Package: $NEW_PACKAGE"
echo "📂 Target Directory: $TARGET_DIR"

# 1. Clean Copy
mkdir -p "$TARGET_DIR"
cp -r "$PROJECT_ROOT/kupu-starter/." "$TARGET_DIR/"
rm -rf "$TARGET_DIR/target"
rm -rf "$TARGET_DIR/.git"
rm -f "$TARGET_DIR/nb-configuration.xml"
rm -rf "$TARGET_DIR/src/main/webapp/WEB-INF/lib/"*

# 2. Rename Directory Structure
OLD_PATH=$(echo $DEFAULT_PACKAGE | tr . /)
NEW_PATH=$(echo $NEW_PACKAGE | tr . /)

# Function to safely move package contents
move_package() {
    local base=$1
    local old_p=$2
    local new_p=$3
    
    if [ -d "$base/$old_p" ]; then
        mkdir -p "$base/$new_p"
        cp -r "$base/$old_p/." "$base/$new_p/"
        # Clean up old empty folders upward but stop before src/main/java
        rm -rf "$base/$old_p"
    fi
}

move_package "$TARGET_DIR/src/main/java" "$OLD_PATH" "$NEW_PATH"
move_package "$TARGET_DIR/src/test/java" "$OLD_PATH" "$NEW_PATH"

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
