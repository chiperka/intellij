# Spark Test Runner — JetBrains Plugin

Official JetBrains plugin for [Spark](https://spark.finie.io/) — a declarative, YAML-based API & integration test runner.

**[Install from JetBrains Marketplace](https://plugins.jetbrains.com/plugin/30418-spark-test-runner)**

## JetBrains Plugin

Run `.spark` tests directly from your editor with a single click.

- **Gutter play buttons** — run individual tests or entire suites
- **Test Runner integration** — pass/fail status, duration, diff viewer for assertion failures
- **Source navigation** — click a test result to jump to its definition
- **Run configurations** — Local, Docker, and Docker Compose execution modes
- **Cloud mode** — run tests on a remote Spark API server
- **Schema validation** — autocompletion and validation for `.spark` and `spark.yaml` files
- **Structure view** — navigate suite > tests > sections
- **Test button** — verify your interpreter setup from Settings

Works with PhpStorm, IntelliJ IDEA, WebStorm, GoLand, PyCharm, and other JetBrains IDEs (2025.1+).

**[Plugin documentation](https://spark.finie.io/ide-plugin)**

## Spark CLI

Spark is a declarative test runner. You define services, HTTP requests, and assertions in `.spark` files and Spark handles the rest — spinning up Docker containers, running healthchecks, executing requests, and evaluating assertions.

Docker is required for local use. Cloud mode (`--cloud`) runs tests on a remote server and does not require Docker locally.

**[Getting started](https://spark.finie.io/getting-started)** · **[GitHub](https://github.com/finie-io/spark-intellij)**
