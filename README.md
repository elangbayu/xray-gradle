# XRAY Gradle Plugin

The XRAY Gradle Plugin is a plugin that integrates with the XRAY Test Management tool to enable seamless test
execution and reporting.

## Features

- Download test scenarios from XRAY based on a specified tag.
- Upload test execution results to XRAY.
- Easy integration into Gradle build scripts.

## Prerequisites

- Java Development Kit (JDK) 8 or higher.
- Gradle 6.0 or higher.
- XRAY Test Management account with API access.

## Installation

To use the XRAY Gradle Plugin in your project, follow these steps:

1. Add the following to your `build.gradle` file:

```groovy
plugins {
    id 'xray-gradle' version '1.0.0'
}
```

2. Configure the plugin with your XRAY credentials, create `.env` file:

```dotenv
XRAY_URL="https://your-xray-provider-server.com"
XRAY_CLIENT_ID="YOUR_XRAY_CLIENT_ID"
XRAY_CLIENT_SECRET="YOUR_XRAY_CLIENT_SECRET"
XRAY_PROJECT_KEY="YOUR_JIRA_PROJECT_KEY"
```

## Usage

### Downloading Test Scenarios

To download test scenarios from XRAY, use the following command:

Mac:

```shell
./gradlew xray -Daction='download' -Dscenario='TAG'
```

Windows:

```shell
.\gradlew xray -'Daction=download' -D'scenario=TAG'
```

Replace `TAG` with the desired tag for filtering the test scenarios.

The downloaded test scenarios will be saved as .feature files in the src/test/resources/features directory.

### Uploading Test Execution Results

To upload test execution results to XRAY, use the following command:

Mac:

```shell
./gradlew xray -Daction='upload' -Dscenario='RESULTS_FILE'
```

Windows:

```shell
.\gradlew xray -D'action=upload' -D'scenario=RESULTS_FILE'
```

Replace RESULTS_FILE with the path to the test execution results file (e.g., cucumber.json).
The test execution results will be uploaded to XRAY, and the corresponding test cases will be updated.

## Contributing

Contributing
Contributions to the XRAY Gradle Plugin are welcome! If you find any issues or have suggestions for improvements, please
open an issue or submit a pull request on the GitHub repository.

## License

This project is licensed under the MIT License.
