# ChatGPT Demo App for Blackberry OS 7

## Screenshot
<div align="center">
  <img src="./screenshots/screenshot-main.png" height=240 alt="Chat with LLM">
  <img src="./screenshots/screenshot-chat.png" height=240 alt="Main screen">
</div>

## Updates

- Deploy your own proxy server to use official ChatGPT API: see [doc](https://github.com/slashbade/ChatGPTDemo/blob/fafe799348557a3cb85c49cab6b849c13f80aa4f/docs/Dev/Deploy%20your%20proxy%20server.md)
- Develop Blackberry OS 7 app in VS Code: see [doc](https://github.com/slashbade/ChatGPTDemo/blob/fafe799348557a3cb85c49cab6b849c13f80aa4f/docs/Dev/Develop%20in%20VS%20code.md)

## Installation

There are two ways to install this app on your Blackberry OS 7 device.

### OTA

- Open this [link](http://bbchatgpt.slashblade.top/) in Blackberry OS browser.
- Click on the link to install the app.

### Compile from source

- Download the source code from release page.
- Open the project in Blackberry Java Plug-in and connect your Blackberry device.
- Compile the project and click on "load package on the device" button.

## Usage

- Open the app and go to the settings page, enter your instance URL and API key.
    - Now official ChatGPT API is not available due to TLS 1.3 support issue, so you need to temporarily use a proxy or self-hosted API.
- Click on the "Save" button to save the settings.
- Go back to the main page and create a new chat.

## Development

This app is still under development and open to contribution. Please feel free to open an issue or submit a pull request. If you want to do some development, please make sure you have the prerequisites installed:

- JDK 1.6.0 update 45
- Blackberry Java Plug-in with Blackberry JRE 7.1.0
