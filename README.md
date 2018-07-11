# Watson Speech SDK Plugin for iOS and Android with Parameters v1.1.0
[![Language: Objective-C](https://img.shields.io/badge/objective--c-2.0-orange.svg?style=flat)](https://en.wikipedia.org/wiki/Objective-C)
[![Language: Java](https://img.shields.io/badge/java-android-orange.svg?style=flat)](http://java.com/)
[![Language: JavaScript](https://img.shields.io/badge/javascript-es5.0-orange.svg?style=flat)](https://www.javascript.com/)
[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/CognitiveBuild/WatsonSpeechPlugin/master/LICENSE)

# Installation

To install the Watson Speech Plugin in your Cordova Project, run in terminal:
```shell
cordova plugin add https://github.com/foward/WatsonSpeechPlugin.git
```

* Example App to test the plugin you will find in this repository under the folder ExampleCordovaApp

# Credentials

* Provide a VCAP_OBJECT_OPTIONS (JSONArray) in the following structure (First you get the VCAP from bluemix and then you add to the list an object called "options" with the same structure as below)
```javascript
        var VCAP_OBJECT_OPTIONS =[

            {
            "speech_to_text": [{
                "credentials": {
                    "url": "https:\/\/stream.watsonplatform.net\/speech-to-text\/api",
                    "username": "<USERNAME_FROM_VCAP>",
                    "password": "<PASSWORD_FROM_VCAP>"
                },
                "syslog_drain_url": null,
                "volume_mounts": [],
                "label": "speech_to_text",
                "provider": null,
                "plan": "lite",
                "name": "<SERVICE_NAME_FROM_VCAP>",
                "tags": ["ibm_created", "ibm_dedicated_public", "lite", "watson"]
            }]
        },{
            "options": {
                "inactivityTimeout" : 30,
                "continuous":false,
                "interimResults": true,
                "languageModel": "en-US_BroadbandModel",
                "audioFormat" : "audio/ogg;codecs=opus",
                "audioSampleRate": 16000,
                "isAuthNeeded": true,
                "isSSL": true,
                "connectionTimeout": 30000,
                "keywordsThreshold": "-1.0D",
                "wordAlternativesThreshold" : "-1.0D",
                "smartFormatting" : false,
                "timestamps" : false,
                "profanityFilter" : true,
                "wordConfidence" : false,
                "apiEndpoint" : "https://stream.watsonplatform.net/speech-to-text/api",
                "apiURL" : "",
                "xWatsonLearningOptOut" : false

            }
        }

        ];
```

# Basic JavaScript APIs

## Speech-to-Text

Always before call the method recognize first call the method "init"
```javascript
        WatsonSDK.SpeechToText.init(VCAP_OBJECT_OPTIONS,function(data){
            if(data){
                console.log("Init Successful")
            }
        });
```
```javascript
WatsonSDK.SpeechToText.recognize(function(data){
    // data
    if(data.iscompleted === WatsonSDK.Constants.YES) {
        // connection closed, ready for another round of speech recognition
        return;
    }

    if(data.isfinal === WatsonSDK.Constants.YES) {
        // last transcript is returned, the WebSocket is automatically disconnected
    }
    // evaluate the transcription
    console.log(data.message);
}, function(error){
    // error
});
```
## Text-to-Speech

```javascript
var text = 'Hello World!';
WatsonSDK.TextToSpeech.synthesize(function(data){
    // success
}, function(error){
    // error
}, [text]);
```
## Text-to-Speech with Customizations

```javascript
var text = 'Hello World!';
var customization_id = 'your-customization-id';
WatsonSDK.TextToSpeech.synthesize(function(data){
    // success
}, function(error){
    // error
}, [text, customization_id]);
```

# Uninstall

To uninstall the Watson Speech Plugin from your Corodva Project, run in terminal:
```shell
cordova plugin remove WatsonSpeechPlugin
```

# License
Copyright 2017 GCG GBS CTO Office under [the Apache 2.0 license](LICENSE).
