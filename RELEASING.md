# Releasing mcpx4j

- Releases are kicked off by the [`release`][release] GitHub Action Workflow.
- The workflow takes one parameter, the version:
   - `version` is a `x.x.x` version number (no `v` prefix) or a `x.x.x-SNAPSHOT` version for snapshot release (e.g. `999-SNAPSHOT` or `1.0.0-SNAPSHOT`)
- When the release process is triggered, the project is built, and all the tests are run.
- If the run is successful, then it is published to Maven Central. This usually takes approximately 20 minutes; the build will wait to exit until the package is acknowledged to be published
- If publishing is successful, then **a git tag `x.x.x` is automatically created and pushed to the `main` branch**
- NOTE: if the `version` is a snapshot release, then NO tag is created! 

[release]: https://github.com/dylibso/mcpx4j/actions/workflows/release.yaml
