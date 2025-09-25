#!/bin/bash

# Spear Code Style Check Script
# This script runs scalastyle to check code style compliance

set -e

echo "Running Scalastyle code style check..."

# Change to project root directory
cd "$(dirname "$0")/.."

# Run scalastyle
sbt scalastyle

echo "Scalastyle check completed successfully!"
