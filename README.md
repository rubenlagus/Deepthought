#  Deepthought 

Telegram Client sample in Java

## Contributions

Feel free to fork this project, work on it and then make a pull request to **dev branch**. 

### Telegram API implementation

This project is using the implementation present at [TelegramApi project](https://github.com/rubenlagus/TelegramApi)

### Adding new plugins

Make a pull request with the new plugin. Please follow these rules:

  1. Add the plugin under *plugins* package in its own package.
  2. Don't make a plugin dependent on a different one.
  3. A nice description of the plugin is welcome inside the package in a file called **Plugin.md**
    

Please, **DO NOT PUSH ANY API KEY OR API HASH**, I will never accept a pull request with that content.


## Usage

Current *Deepthought.java* has a example of usage with echo bot, to run it you need a couple of steps first
    
  1. In *Deepthought.java*, set up your own APIKEY and APIHASH. You can get them [here](https://core.telegram.org/api/obtaining_api_id)
  2. In *DatabaseConstants.java*, set up your database configuration (database name, user, password). Currently the code support mysql databases.

In any case, this is the things that needs to be done before using it:

  1. In *Deepthought.java*, set up your own API_KEY and API_HASH. You can get them [here](https://core.telegram.org/api/obtaining_api_id)
  2. In the plugin you want to build, create your own class that extends *UpdatesHandlerBase.java* (or *DefaultUpdatesHandler.java* if you only need a simpler version). This will be in charge of handling every update received.
  3. In the plugin you want to build, create your own class that implements *ChatUpdatesBuilder.java*. This builder must be the one in charge of creating the unique instance of your previous handler.
  4. For building points 2 and 3, you are gonna need a DatabaseManager implementation (this doesn't need to have a real database behind it, but it must be able to store *chats*, *users* and *differences data* somewhere so the client works correctly.
  5. In *Deepthought.java* build your own BotConfig with your phone number (remember that it must be in international format +1XXXXXXXXX, but without the *+* symbol) and the name of the auth file where the session must be stored. Then build your own ChatUpdatesBuilder implementation and finally create your own *TelegramBot.java* and call its methods *init* and *startBot*.
  6. It should start working.
    
    
## Example bots

Create your own stickers with [Clippy](https://telegram.me/clippy)
    
## Telegram API and MTPROTO:

If you want more information about Telegram API, you can go [here](https://core.telegram.org/api#telegram-api). And [here](https://core.telegram.org/mtproto) you will find extra information about mtproto.
 
## Questions or Suggestions
Feel free to create issues [here](https://github.com/rubenlagus/Deepthought/issues) as you need

## License 
MIT License

Copyright (c) 2016 Ruben Bermudez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
