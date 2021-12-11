# Prox Professor Profile Service

The purpose of this service is to provide a backend for using professor profiles in PROX.

## Installation

After a `git clone` or download the project the following command must be executed once to initialize the projects.

Windows (CMD/PowerShell)

```posh
# Switch to project folder
cd .\prox-professor-profile-service\
# Execute initial build for git hooks...
.\mvnw.cmd clean test
```

Linux/MacOS (Bash/Terminal)

```bash
# Switch to project folder
cd prox-professor-profile-service/
# Execute initial build for git hooks...
./mvnw clean test
```

Executes the [Maven default lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) up to the `test` phase. During the `package` phase, an executable JAR and the Docker image are created.

## Local usage with docker

A Docker network named `prox` is required for the communication between services:

```bash
docker network create prox
```

Starts a Docker container based on the compose file and the image.

Powershell

```posh
$env:IMAGE='prox-professor-profile-service'; `
$env:TAG='latest'; `
docker-compose -f ./src/main/docker/docker-compose.yml up
```

Bash/Shell

```bash
export IMAGE="prox-professor-profile-service" &&
export TAG="latest" &&
docker-compose -f ./src/main/docker/docker-compose.yml up
```

## Local usage in IntelliJ IDEA

For the necessary steps please look in [Run/Debug in IntelliJ IDEA](https://github.com/Archi-Lab/prox-local-setup#rundebug-in-intellij-idea).

## About the Team

This service is currently developed by members of the ArchiLab staff:

- Julian Lengelsen ([@jlengelsen](https://github.com/jlengelsen))

## Development

Please consider our [contributing guidelines](./CONTRIBUTING.md).

### Commit Messages

The commit messages **SHOULD** adhere to the
[Conventional Commits specification](https://conventionalcommits.org/). This
repository is also
[Commitizen](https://github.com/pocommitizen/cz-cli)-friendly. You can use
Commitizen seamless in this repository.

### Perform a release

In general releases are done by pushing a git tag which conforms to
[SemVer](https://semver.org/) specification. We prefix those tags with a `v`, so
the tag itself **MUST** follow the pattern `vMAJOR.MINOR.PATCH`. A label is not
used.

The simplest way to perform a release is by relying on our
[standard-version](https://github.com/conventional-changelog/standard-version)
configuration. If you are ready to perform a release simply call

```shell
$ npx standard-version
# or
$ npm run release
```

This will analyze the last commits since the last release, determine the new
version, generate a changelog and create a git tag for you. Next up you will
need to push the tag and version bumped files, our release pipeline will take
care of the rest.

```shell
$ git push --follow-tags origin main
```