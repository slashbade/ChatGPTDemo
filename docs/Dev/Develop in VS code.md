# Develop with BBOS 7 SDK in Visual Studio Code

## Step 1: Install the BBOS 7 SDK

Configure your development environment by installing official Eclipse-based BBOS 7 SDK. See link for more details: [Java for BlackBerry OS](https://archive.org/details/java-for-blackberryos)

## Step 2: Create a New Project or Open an Existing Project

You are recommended to use the Eclipse IDE to create a new project or open an existing one. The BBOS 7 SDK is designed to work with Eclipse, and it provides the necessary tools and libraries for BlackBerry OS development.

## Step 3: Set Up Visual Studio Code

1. **Install Visual Studio Code**: Download and install Visual Studio Code from [the official website](https://code.visualstudio.com/).
2. **Install Java Extensions**: Since Blackberry OS 7 SDK uses very old Java versions, direct support may not be available. However, you can get basic functionality by installing the following pack:
   - [Java Coding Pack](https://code.visualstudio.com/docs/languages/java)
3. **Configure VS Code settings**: Open the workspace settings in VS Code and configure `settings.json` to include the path to the BBOS 7 SDK libraries. Here’s an example configuration:

    ```json
    {
        "java.home": "C:\\Eclipse\\jre",
        "java.project.referencedLibraries": {
            "include": [
                "C:\\Eclipse\\plugins\\net.rim.ejde.componentpack7.1.0_7.1.0.10\\components\\lib\\net_rim_api.jar"
            ]
        }
    }
    ```

4. **Configure classpath settings (Important)**: Such configuration will let you see the javadocs and results in better code completion. Append to the `.classpath` file in your project root:

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <classpath>
        <classpathentry kind="src" path="src"/>
        <classpathentry kind="src" path="res"/>
        <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/net.rim.ejde.BlackBerryVMInstallType/BlackBerry JRE 7.1.0"/>
        <classpathentry kind="lib" path="C:/Eclipse/plugins/net.rim.ejde.componentpack7.1.0_7.1.0.10/components/lib/net_rim_api.jar">
            <attributes>
                <attribute name="javadoc_location" 
                    value="file:C:/Eclipse/plugins/net.rim.ejde.componentpack7.1.0_7.1.0.10/components/docs/api"/>
            </attributes>
        </classpathentry>
        <classpathentry kind="output" path="bin"/>
    </classpath>
    ```

## Step 3: Configure Build and Run Tasks

TBD
