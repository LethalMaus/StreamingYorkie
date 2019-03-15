# StreamingYorkie

<p align="center">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/streaming_yorkie-web.png"><br><br>
</p>

---

## Table of Contents

+ [Introduction](#introduction)
+ [Guide](#guide)
  + [Login & Logout](#login--logout-)
  + [Followers, Following & F4F](#followers--following---f4f-)
    + [Menu Categories](#menu-categories)
    + [Catgory Actions](#category-actions)
  + [User Info](#user-info-)
  + [Info](#info)
    + [Logs](#logs)
  + [Settings](#settings)
+ [Updates](#updates)
  + [1.0.0-a](#100-a)
+ [Roadmap](#roadmap)
+ [Reported Bugs & Problems](#reported-bugs--problems)
+ [Contact](#contact)

---

## Introduction

Streaming Yorkie is designed to help *Twitch Streamers* to efficiently *Follow & Unfollow* other Streamers as well as offering a better overview of *Followers & Following*.
Automatically *Follow, Unfollow & F4F* simply by just installing and configuring Streaming Yorkie with help from this guide.
Helps build a bigger community by simplifying communication & freeing time to stream more.
Streaming Yorkie can be found in the [Google Play Store].

The code is **Open Source**, contains **no costs & no advertisements** are involved. Streaming Yorkie is a gift back to a great community who I will continue to support for free.
Streaming Yorkie communicates exclusivly with the *Twitch API*. A login is required to be able to edit your *Followers & Following*.
Passwords are not saved, rather a unique Token given by Twitch is saved. This ensures that your account is as safe as can be.

Streaming Yorkie was developed with *Android Studio* in *Java*. *Lint* was used to acquire high quality code & the code itself is documented with *JavaDocs* & comments.
**DRY** *(Dont Repeat Yourself)* & **KISS** *(Keep It Simple Stupid)* principles are enforced as much as possible. Variable naming conventions are in place. 

> The variable name must explain what it is or what it does, clearly, for any developer to understand.

Hopefully this can be used as an example for good coding as well as how to develop an Android App in Java.
Any collaborations are welcome, so feel free to fork & ask for merge requests.
See something inefficient? [Let me know](#contact). I'm always trying to improve my code & learn new things.

Currently it is in an **Alpha** state. 
It could be that a *Bug* may appear or the app behaves in an unexpected manner.
Please report any problems or suspiscions to help improve Streaming Yorkie for everyone.

---

## Guide

### Login & Logout <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/authorization.png" height="40" width="40">

Here you will be asked for your *Username & Password* which will be given directly to *Twitch*.
Streaming Yorkie needs to be authorized to be allow you to edit & change you account.
Logging in is required only once & without it, it cannot work.
Once logged in you should see you own Logo. From here press back to get to the menu & you're good to go.

To logout click on the *options* in the *Support Bar* (top right) & then on the *Logout*.

**Once logged out, all your data will be deleted from the device.**

---

### Followers <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/followers_menu.png" height="40" width="40">, Following <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_menu.png" height="40" width="40"> & F4F <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/f4f_menu.png" height="40" width="40">

Here you can see who you follow (a.k.a. *Following*), who follows you (a.k.a. *Followers*), who unfollowed you & lots more.

To *refresh* the view & send a new request press the refresh button <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/refresh.png" height="20" width="20">

#### Menu Categories
Each Menu is split into 4 of the following categories:
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/new_button.png" height="20" width="20"> All new Followers/Following
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/follow.png" height="20" width="20"> All current Followers/Following (including New)
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/unfollow.png" height="20" width="20"> All Followers who Unfollowed you or Following you Unfollowed
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/excluded.png" height="20" width="20"> Users that are excluded from AutoFollow as well as being excluded from the other menus
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/notfollowing_followers.png" height="20" width="20"> Users who Follow you, but you dont Follow them back
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/follow4follow.png" height="20" width="20"> Followers who also Follow you
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_nonfollowers.png" height="20" width="20"> Users you Follow, who dont Follow back

#### Category Actions
Each category contains up to 3 of the following actions:
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/follow.png" height="20" width="20"> Follows the chosen User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/unfollow.png" height="20" width="20"> Unfollows the chosen User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/notifications.png" height="20" width="20"> Activates notifications received from the User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/deactivate_notifications.png" height="20" width="20"> Deactivates notifications received from the User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/excluded.png" height="20" width="20"> Excludes Users from other categories & from AutoFollow
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/include.png" height="20" width="20"> Includes Users from other categories & from AutoFollow
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/delete.png" height="20" width="20"> Deletes User from the Device (all users, even the unfollowed ones are saved until otherwise)

In F4F at the end of <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/notfollowing_followers.png" height="20" width="20"> or <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_nonfollowers.png" height="20" width="20"> there is an option to **Follow/Unfollow all users** within the category.

---

### User Info <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/userinfo.png" height="40" width="40">

This is an overview of the *User* who is currently logged in, that shows the following:
+ **Logo:** Your profile picture
+ **Username:** Your login name / user name
+ **ID:** Your unique Twitch ID
+ **Game:** The current game you are playing
+ **Member Since:** When you joined
+ **Views:** How many views you have
+ **Followers:** How many followers you have
+ **Broadcaster Type:** If you are affiliated or partnered
+ **Status:** Your Go Live status
+ **Description:** Your profile description

To *refresh* the view & send a new request press the refresh button <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/refresh.png" height="20" width="20">

If you would like anything else to be displayed, or have the info display differently, [let me know](#contact).

---

### Info <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/info.png" height="40" width="40">

Here you can find external links in relation to the Streaming Yorkie & its Developer.
 
Source Code & Documentation

<a href="https://github.com/LethalMaus/StreamingYorkie">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/github.png" height="60">
</a><br><br>

Support & Descriptive Animals Community

<a href="https://discord.gg/asZsz2F">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/discord.png" height="60"> 
</a><br><br>

Live Streams & Entertainment

<a href="https://www.twitch.tv/lethalmaus">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/twitch.png" height="60">
</a><br><br>

Membership & Partnership

<a href="https://www.patreon.com/LethalMaus/creators">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/patreon.png" height="60">
</a><br><br>

Donations (not expected, but welcomed)

<a href="https://paypal.me/JamesCullimore/2,50">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/paypal.png" height="60">
</a><br><br>

#### Logs
Tapping the *Developer Logo* **8** times will give you access to the app logs & files. An error log is available along with much more for support.

---

### Settings <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/settings.png" height="40" width="40">

Here you can configure the *AutoFollow* to run in the backround.

+ **AutoFollow Service:** *Following, Unfollowing or Following & Unfollowing* can be activated.

+ **AutoFollow Interval:** The interval slider ranges from *1-60* with a unit option of *Minutes, Hours or Days*.
Please be wary, due to a high battery consumption & inefficiency from other Apps, the interval has been restriced by Android to a minimum of 15 Minutes. Going below this will default back to 15 Minutes.

+ **AutoFollow Enable Notifications:** You can switch on or off whether the *AutoFollow* activates notifications for each new Follower.

+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/save.png" height="20" width="20"> **Save:** Once you are done and changes have been made, it will be saved locally as a File.

> **Warning**
>
> Please make sure you have excluded Followers & Following you wish to be left alone from the AutoFollow Service.

If you run into problems with the AutoFollow Service, deactivate it & [contact](#contact) me.

---

## Updates

### 1.0.0-a

First Public Major Release.

Features:
+ User Overview
+	Follower, Following & Follow4Follow Overview
+	Developer Overview
+	Settings implemented
+	AutoFollow implemented
+	Login/Logout implemented

Bug Fixes:
+	User list view lag
+	User list order wrong
+	Action buttons funtionality fix
+	AutoFollow activation fix
+	Multiple request collision fix
+	Skipped frames fix

---

## Roadmap

The following tasks and features are currently on the roadmap & some of which are likely to be within the next release.

+ Apply Themes, Color, Fonts & Logo throughout app
+ Redesign info.xml to rid of nested weights
+ Implement JaCoCo code coverage
+ Implement JavaDoc restrictions
+ All weak references need to be checked for availability
+ Unit tests
+ Data backup & recovery
+ File Observer to renew changes when in User view
+ Stream Scheduler (for past, current & future streams)
+ Auto VOD Exporter
+ Host for Host
+ Status, Game, Tags & Description update 
+ Reusable Activities (Go Live text, game, tags)
+ Instant AutoFollow & AutoUnfollow
+ Stream Info (views, hosts, followers, chats)
+ Stream Lurker (audio only mode)
+ Streaming tips (obs settings, camera & green screen, networking, chat interactions)
+ Handle Muliple accounts
+ i18n (Internationalization)

They are ordered in priority. If you would prefer something to be higher on the list, [let me know](#contact)

---

## Reported Bugs & Problems

Any *bugs, problems, Code inefficiencies* will be listed here.
Please give a **detailed description** as to how to reproduce the problem.

| **Problem** | **Reproduction** | **Version** | **From** |
|:----------- |:---------------- |:-----------:|:--------:|
| Request timeout takes too long in VM (test on local device) | n/a | 1.0.0-a | LethalMaus |
| Authorization connection timeout not working | n/a | 1.0.0-a | LethalMaus |
| Settings should be an Async Task | n/a | 1.0.0-a | LethalMaus |
| User request is sent twice (saw twice, needs reproduction) | Has relation to weak Internet when starting Main Activity | 1.0.0-a | LethalMaus |
| On rotation activity refreshes | n/a | 1.0.0-a | LethalMaus |

---

## Contact

For any issues, request for changes or questions, feel free to ask. I will get back to you as soon as I can.

<a href="https://discord.gg/asZsz2F">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/discord.png" height="60"> 
</a><br><br>

I also love to entertain, game & stream as a hobby. If you're interested, come & say 'Hi'

<a href="https://www.twitch.tv/lethalmaus">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/twitch.png" height="60">
</a><br><br>

Any support or donations are highly appreciated (but not expected) & go towards improving development & entertainment.

<a href="https://www.patreon.com/LethalMaus/creators">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/patreon.png" height="60">
</a><br>

<a href="https://paypal.me/JamesCullimore/2,50">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/paypal.png" height="60">
</a><br><br>

I'm on [LinkedIn](https://www.linkedin.com/in/james-cullimore-042ab397/). Here you will find my Development preferences & experiences.

---
