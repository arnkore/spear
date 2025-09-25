# Spear Makefile
# Provides convenient commands for building and testing the project

.PHONY: help clean compile test style package build all

# Default target
help:
	@echo "Spear Build Commands:"
	@echo "  make clean     - Clean previous build artifacts"
	@echo "  make compile   - Compile source code"
	@echo "  make test      - Run all tests"
	@echo "  make style     - Run scalastyle code style check"
	@echo "  make package   - Create JAR package"
	@echo "  make build     - Run complete build (clean + compile + test + style + package)"
	@echo "  make all       - Alias for 'make build'"

clean:
	sbt clean

compile:
	sbt compile

test:
	sbt test

style:
	sbt scalastyle

package:
	sbt package

build: clean compile test style package
	@echo "Build completed successfully!"

all: build
