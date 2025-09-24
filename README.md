# Crucible

The Crucible testing framework - a comprehensive mono-repo containing Java libraries for testing infrastructure and utilities.

## Overview

This repository contains multiple Java libraries designed to provide robust testing capabilities and utilities. Each library is independently versioned but maintained within this unified mono-repo structure.

## Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- Git

## Installation

### Clone the Repository

```bash
git clone https://github.com/EliassenInnovation/crucible.git
cd crucible
```

### Install to Local Maven Repository

To build and install all libraries to your local Maven repository:

```bash
mvn clean install
```

This will:
- Compile all modules
- Run tests
- Package the libraries
- Install them to your local Maven repository (`~/.m2/repository`)

### Skip Tests During Installation

Since Crucible is a behavioral testing tool, some behavioral tests may be included in the test suites that you may want to skip during installation:

```bash
mvn clean install -DskipTests
```

This is useful when:
- You want a faster installation process
- Behavioral tests require specific environment setup
- You're installing in an environment where tests shouldn't run

### Install Specific Modules

To install only specific modules, navigate to the module directory and run:

```bash
cd [module-name]
mvn clean install
```

Or to skip tests for specific modules:

```bash
cd [module-name]
mvn clean install -DskipTests
```

## Artifact Feed

📦 **Coming Soon**: We will be adding an artifact feed (Maven repository) in the near future to make consuming these libraries even easier. This will allow you to include Crucible libraries as dependencies without needing to build from source.

## Version Management

To update the version across all modules in the mono-repo:

```bash
mvn versions:set -DnewVersion=2.0.0
```

This command will update the version in all `pom.xml` files throughout the repository.

To commit the version changes:

```bash
mvn versions:commit
```

To revert version changes (if needed):

```bash
mvn versions:revert
```

## Project Structure

```
crucible/
├── pom.xml                 # Parent POM
├── module-1/              # Individual library modules
│   └── pom.xml
├── module-2/
│   └── pom.xml
└── ...
```

## Usage

After installation, you can include any of the Crucible libraries in your projects by adding the appropriate dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.eliasseninnovation.crucible</groupId>
    <artifactId>[module-name]</artifactId>
    <version>[version]</version>
</dependency>
```

## Development

### Building

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Creating a Release

1. Update version numbers: `mvn versions:set -DnewVersion=X.Y.Z`
2. Commit version changes: `mvn versions:commit`
3. Build and test: `mvn clean install`
4. Create and push tag: `git tag vX.Y.Z && git push origin vX.Y.Z`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[Add your license information here]

## Support

For questions, issues, or contributions, please:
- Open an issue on GitHub
- Contact the development team
- Check the documentation in each module's directory

---

**Crucible** - Building robust testing infrastructure, one library at a time.