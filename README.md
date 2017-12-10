# Java Messenger Watchdog

The Java Messenger Watchdog helps you to keep an eye on your Websites. As soon as a watched site
is down, the watchdog will bark at you on Facebook Messenger.

[![Build Status](https://travis-ci.org/chatbot-workshop/java-messenger-watchdog.svg?branch=master)](https://travis-ci.org/chatbot-workshop/java-messenger-watchdog)

This project was used for a workshop I gave for the ![Java User Group Switzerland](https://www.jug.ch/).
Therefore this readme is made to guide you through the process of bringing this chatbot to life and
continue to implement your own ideas.

## Homework before the workshop starts
There are some things you have to do before we can start the workshop.

> This is very important because in some cases you have to wait for Facebook approval for a day or so. 
> So we can't do this just at the workshop itself.

The homework consists of following things you should create before the workshop:

- Facebook account
- Facebook developer account
- Facebook page
- Facebook app

To make us more efficient at the workshop you should also be familiar with:

- The java programming language
- Your IDE (preferably IntelliJ IDEA or Eclipse)
- Gradle as your build tool

### Create a Facebook account
I think I don't have to explain this step. I just wanted to be clear on this that you need it.

### Create a Facebook developer account
Go to [https://developers.facebook.com](https://developers.facebook.com) and create a developer
account.

### Facebook page
Your chatbot will run on a Facebook page. Therefore we create our own page for this bot. Go to
[Facebook](https://www.facebook.com/) and click the icon on the top left corner:

![Facebook Icon](createFacebookPage0.png)

Now you can create a page by clicking at the verry bottom of the left menu:

![Click create page](createFacebookPage1.png | width=200)
 

### Create a Facebook app
This step is a bit more complicated. Therefore we will go through it step by step.

First go to [https://developers.facebook.com](https://developers.facebook.com) with your newly created
developer account. Click on `My Apps` and then `Add a New App`.
![create Facebook App](createFacebookApp.png)

Now enter the display name and contact e-mail of your app and click `Create App ID`.
![Create App ID](createANewAppId.png)



## Broad overview

We will build a chatbot that integrates with facebook as described in the following diagram:

![Productive Deployment](ProductiveDeployment.png)

1. When you will setup your webhook, Facebook will fist call your URL using an http GET request to 
verify that is you who wanted to set the webhook to this url. You should answer with an http status
200 if the webhook may be configured.

2. Then all events from Facebook Messenger will be sent to your webhook using http POST requests.

3. If you want to send something back to your user, you have to make calls back to Facebook.

As a webhook you can only declare a https connection with a valid SSL certificate. Therefore you need to 
have a valid certificate on your machine. This can be setup with the project [ngrok](https://ngrok.com/).
In this case the deployment looks a bit different:

![Development Deployment](DevelopmentDeployment.png)

You can run your local `ngrok` process with following command:

```
$ ngrok http 8080
```

It will output following lines:

```
ngrok by @inconshreveable                                (Ctrl+C to quit)

Session Status                online
Version                       2.2.8
Region                        United States (us)
Web Interface                 http://127.0.0.1:4040
Forwarding                    http://797af51b.ngrok.io -> localhost:8080
Forwarding                    https://797af51b.ngrok.io -> localhost:8080
```

Now you have your own ngrok domain and certificate. Just use following url als your webhook:
`https://797af51b.ngrok.io/callback`

### Connect app and page
Now your app needs to be connected to your page. Therefore click `add product` and choose
`Messenger`.

At `Key generation` you can choose your site and let facebook generate a **page access token**. You
need this token later.

The next section is about webhooks. You should create a webhook and enter following parameters:

- Webhook-URL: The URL to your server. For example `https://my-app.mydomain.com/callback`. If you 
  run your app on your local development machine you should use `ngrok`.

- **Verify token**: A token string of your choice. Facebook will send you this token to verify the
  webhook setting. This protects you against other sites who may want to use your bot without your
  permission.

- Finally you choose your field subscriptions. Here you can define which events should be forwarded
  to your webhook.
  
Finally you should go back to the app dashboard and find the **app secret**. You will need it in your
java project.

## Run this app

This app uses [gradle](https://gradle.org/) to build. You can import the project to your Eclipse
or IntelliJ. To run the app you simply start the `main` method from
`ch.apptiva.watchdog.Application` with following environment variables:

| Variable | Description |
| ---- | ---- |
|`messenger.pageAccessToken`| The previously generated access token from your Facebook app. This token makes sure, that only you can post messages in the name of your Facebook page. |
|`messenger.appSecret`| You find this secret on your Facebook app Dashboard. It identifies you as the owner of the app. |
|`messenger.verifyToken`| This token is sent to you when someone sets a new webhook to your url. It makes sure no one else can use your process to run his own chatbot. |

## Architecture overview

I tried to apply a [Hexagonal Architecture Style](http://alistair.cockburn.us/Hexagonal+architecture)
This made it easy to separate the domain model from the facebook API usage. So you can focus on
how the API is used if you are not interested in the watchdog functionality. 

There are some main packages which I will describe here briefly:

| Package | Description |
| ---- | ---- |
|`ch.apptiva.watchdog.domain` | All the domain stuff that describes how the watchdog logic behaves |
|`ch.apptiva.watchdog.adapter` | Everything technology centric. Like the facebook API, Event Bus... |
|`ch.apptiva.watchdog.adapter.messenger` | Here you find how the facebook API is used. Everything that goes to or comes from Facebook is running through this package. |
|`ch.apptiva.watchdog.adapter.EventBus` | There are some domain events published from the domain logic. This package contains the infrastructure code for this. |
|`ch.apptiva.watchdog.adapter.timer` | Setup for a spring timer which triggers domain logic from time to time. |
|`ch.apptiva.watchdog.adapter.persistence` | The most simple implementation of a repository holding the webpages that watchdog cares about. |

For the rest I hope the code speaks for itself...
