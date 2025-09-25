#!/bin/bash

# Spear Build Script
# This script performs a complete build including compilation, testing, and style checking

set -e

echo "=========================================="
echo "Spear Build Script"
echo "=========================================="

# Change to project root directory
cd "$(dirname "$0")/.."

# Function to print step headers
print_step() {
    echo ""
    echo "=========================================="
    echo "$1"
    echo "=========================================="
}

# Step 1: Clean
print_step "Step 1: Cleaning previous build"
sbt clean

# Step 2: Compile
print_step "Step 2: Compiling source code"
sbt compile

# Step 3: Run tests
print_step "Step 3: Running tests"
sbt test

# Step 4: Code style check
print_step "Step 4: Running code style check (scalastyle)"
sbt scalastyle

# Step 5: Package
print_step "Step 5: Creating package"
sbt package

echo ""
echo "=========================================="
echo "Build completed successfully!"
echo "=========================================="
