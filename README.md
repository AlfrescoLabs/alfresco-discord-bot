# Getting Started

A Discord Bot that provides seamless integration with ACS.

1. Create your [Discord Bot](https://discordnet.dev/guides/getting_started/first-bot.html)
2. Set the `ALFRESCO_BOT_TOKEN` environment variable to be equal to your bot token
3. **Run** with `mvn spring-boot:run`.

> ⚠ **WARNING:**
>
> Remember to either:
> - start a local ACS deployment _(for example
    via [docker-compose](https://github.com/Alfresco/acs-deployment/blob/master/docker-compose/docker-compose.yml))_
> - configure the bot to target your desired environment by tweaking
    the [application.properties](src/main/resources/application.properties) file

### Reference Documentation

For further reference, please consider the following sections:

* [Discord Developer Portal](https://discord.com/developers/docs/intro)
* [Java Discord API](https://github.com/DV8FromTheWorld/JDA)
* [Alfresco Java SDK](https://github.com/Alfresco/alfresco-java-sdk)