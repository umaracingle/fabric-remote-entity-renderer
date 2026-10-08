# Remote Entity Renderer

A Fabric client-side mod for Minecraft Java Edition 26.2 that polls a configured HTTP endpoint and renders remote players/entities in the client world, even when they are outside normal render distance.

## Features

- Configurable GET endpoint via `config/remote-entity-renderer.json`
- JSON payload format:
  ```json
  {"max":125,"players":[{"world":"minecraft_overworld","armor":0,"name":"notch","x":-3954,"y":89,"health":20,"z":2448,"uuid":"069a79f4-44e9-4726-a5be-fca90e38aaf5","yaw":108}]}
  ```
- Entities are rendered as client-side ArmorStand markers
- Native client entity positions are preferred when available
- Entities can render outside the standard entity render distance
- Configurable poll interval (default 5 seconds)

## Setup

1. Edit `config/remote-entity-renderer.json` after first run
2. Set `url` to your HTTP GET endpoint that returns the JSON format above
3. Optionally adjust `pollIntervalSeconds` (minimum 1)
4. Start Minecraft with Fabric installed
5. The mod will fetch and display entities from the configured endpoint

## Building

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Requirements

- Minecraft Java Edition 26.2
- Fabric Loader 0.16.9+
- Java 21+

## How It Works

The mod:
1. Polls the configured HTTP endpoint every N seconds (configurable)
2. Parses the JSON response for a `players` array
3. For each player entry, creates or updates a client-side invisible ArmorStand entity with a custom name
4. Overrides render distance checks to always display the entity
5. Prefers the position of native client entities if they exist nearby (optional fallback)
6. Removes remote entities that are no longer in the API response

## Configuration

File: `~/.minecraft/config/remote-entity-renderer.json`

```json
{
  "url": "https://your-api.com/players",
  "pollIntervalSeconds": 5
}
```

- `url`: The HTTP GET endpoint that returns entity data
- `pollIntervalSeconds`: How often to poll the endpoint (in seconds)
