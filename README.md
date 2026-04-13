# Chiperka — JetBrains Plugin

Official JetBrains plugin for [Chiperka](https://about.chiperka.com/) — a declarative YAML-based specification for describing and running backend projects.

**[Install from JetBrains Marketplace](https://plugins.jetbrains.com/plugin/30418-chiperka-test-runner)**

## What does it do?

This plugin lets you read and edit `.chiperka` specifications with full IDE support, and run build, execute, and test actions directly from the editor. Requires [Chiperka CLI](https://about.chiperka.com/getting-started).

### Specification editing

- **JSON Schema validation** — autocompletion and real-time validation for `.chiperka` and `chiperka.yaml` files
- **Structure view** — navigate suite > tests > sections (services, setup, execution, assertions)
- **File template** — create new specification files from *New > Chiperka Specification*
- **Custom file icon** — `.chiperka` files are instantly recognizable in the project tree

### Build, execute & test

- **Gutter play buttons** — run individual tests or entire suites with one click
- **Test Runner integration** — pass/fail status, duration, diff viewer for assertion failures
- **Source navigation** — click a test result to jump to its definition in the `.chiperka` file
- **Run configurations** — Local, Docker, and Docker Compose execution modes

Works with PhpStorm, IntelliJ IDEA, WebStorm, GoLand, PyCharm, and other JetBrains IDEs (2025.1+).

## Getting started

1. [Install the Chiperka CLI](https://about.chiperka.com/getting-started)
2. Install the plugin and open a project that contains `.chiperka` specification files
3. Configure the Chiperka executable path in **Settings > Tools > Chiperka**
4. Click the play button in the gutter next to a test name to run it

**[Plugin documentation](https://about.chiperka.com/ide-plugin)** · **[GitHub](https://github.com/finie-io/chiperka-intellij)**
