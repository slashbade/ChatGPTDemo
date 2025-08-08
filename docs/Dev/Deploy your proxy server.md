# Deploy your proxy server

## Requirements

- A server with a public IP address (Optional if you just want to use it locally)

## Steps

1. **Install litellm** We use `litellm` to create a proxy server for the LLM API, which provides a clean OpenAI-compatible interface and can disable SSL verification. Install it with the following command: 
    If you can, use a conda environment or virtual environment to avoid conflicts with other packages.
    ```bash
    pip install 'litellm[proxy]'
    ```
    See [litellm doc](https://docs.litellm.ai/docs/simple_proxy) for more details.
2. **Create Configuration** Make a directory and create YAML configuration file named `config.yaml` in it.
3. **Edit Configuration** Open `config.yaml` in a text editor and add the following content:
    ```yaml
    model_list:
      - model_name: gpt-4o
        litellm_params:
          model: openai/gpt-4o
          api_key: os.environ/OPENAI_API_KEY
      - model_name: gpt-5
        litellm_params:
          model: openai/gpt-5
          api_key: os.environ/OPENAI_API_KEY
      - model_name: gpt-5-nano
        litellm_params:
          model: openai/gpt-5-nano
          api_key: os.environ/OPENAI_API_KEY
    litellm_settings:
        ssl_verify: false # KEY CHANGE TO DISABLE SSL VERIFICATION
    ```
    If you want to configure more models, you can refer to the [litellm doc](https://docs.litellm.ai/docs/proxy/configs) for more details.
4. **Run the Proxy Server** Start the proxy server with the following command:
    ```bash
    export OPENAI_API_KEY=your_openai_api_key
    litellm --config config.yaml
    ```

## Test the Proxy Server

You can test the proxy server by sending a request to it. Use the following command to send a test request:
```bash
curl -X POST 'http://0.0.0.0:4000/chat/completions' \
-H 'Content-Type: application/json' \
-H 'Authorization: Bearer sk-1234' \
-d ' {
      "model": "gpt-4o",
      "messages": [
        {
          "role": "user",
          "content": "what llm are you"
        }
      ],
    }
'
```
Note that for gpt-5, you may need to verify your organization in the OpenAI dashboard.

## Accessing the Proxy Server in ChatGPTDemo Blackberry App
Fill in `http://your_server_ip:4000` in the instance URL field, and use `sk-1234` as the API key. You can then use the proxy server in the app.
