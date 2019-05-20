# StreamingYorkie

<p align="center">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/streaming_yorkie-web.png"><br><br>
  A Twitch streamers best friend.
</p>

---

## Table of Contents

+ [Introduction](#introduction)
+ [Guide](#guide)
+ [Updates](#updates)
  + [1.1.1-a](#111-a)
  + [1.1.0-a](#110-a)
  + [1.0.4-a](#104-a)
  + [1.0.3-a](#103-a)
  + [1.0.2-a](#102-a)
  + [1.0.1-a](#101-a)
  + [1.0.0-a](#100-a)
+ [Roadmap](#roadmap)
+ [Issues](#issues)
+ [Contact](#contact)

---

## Introduction

Streaming Yorkie is designed to help *Twitch Streamers* to efficiently *Follow & Unfollow* other Streamers & *export VODS* as well as offering a better overview of *Followers & Following*.
Automatically *Follow, Unfollow, F4F & Export VODs* simply by just installing and configuring Streaming Yorkie.
Helps build a bigger community by simplifying communication & freeing time to stream more.
Streaming Yorkie can be found in the [Google Play Store](https://play.google.com/store/apps/details?id=com.lethalmaus.streaming_yorkie).

The code is **Open Source**, contains **no costs & no advertisements** are involved. Streaming Yorkie is a gift back to a great community who I will continue to support for free.
Streaming Yorkie communicates exclusively with the [Twitch API](https://dev.twitch.tv/) & [Twitch Website](https://twitch.tv). A login is required to be able to edit your Twitch data.
Passwords are not saved, rather a unique Token given by Twitch is saved. This ensures that your account is as safe as can be.

Streaming Yorkie was developed with *Android Studio* in *Java*. *Lint* was used to acquire high quality code & the code itself is documented with *JavaDocs* & comments.
**DRY** *(Don't Repeat Yourself)* & **KISS** *(Keep It Simple Stupid)* principles are enforced as much as possible. Variable naming conventions are in place. 

> The variable name must explain what it is or what it does, clearly, for any developer to understand.

Hopefully this can be used as an example for good coding as well as how to develop an Android App in Java.
Any collaborations are welcome, so feel free to fork & ask for merge requests.
See something inefficient? [Let me know](#contact). I'm always trying to improve my code & learn new things.

Currently it is in an **Alpha** state. 
It could be that a *Bug* may appear or the app behaves in an unexpected manner.
The logic has been tested, however not every view could be tested due to time constraints.
Please report any problems, suspicions or confirmations to help improve Streaming Yorkie for everyone.

---

## Guide

+ [Login & Logout](#-login--logout)
+ [Symbols & Icons](#symbols--icons)
  + [Menu](#menu)
  + [Categories](#categories)
  + [Actions](#actions)
+ [Followers, Following & F4F](#followers-following--f4f)
+ [VODs](#-vods)
+ [User Info](#-user-info)
+ [Info](#-info)
  + [Logs](#logs)
+ [Settings](#-settings)
  + [AutoFollow](#-autofollow)
  + [AutoVODExport](#-autovodexport)

### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/authorization.png" height="30" width="30"> Login & Logout

Here you will be asked for your *Username & Password* which will be given directly to *Twitch*.
Streaming Yorkie needs to be authorized to be allow you to edit & change you account.
Logging in is required only once & without it, it cannot work.
Once logged in you should see you own Logo. From here press back to get to the menu & you're good to go.

To logout click on the *options* in the *Support Bar* (top right) & then on the *Logout*.

**Once logged out, all your data will be deleted from the device.**

---

### Symbols & Icons

To help towards internationalization (i18n) I decided to use symbols/icons that were inspired by the actions taken on Twitch.

#### Menu
Several menus are available to divide StreamingYorkie into each feature it offers:
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/followers_menu.png" height="20" width="20"> Shows all *Followers* from Twitch including who has unfollowed
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_menu.png" height="20" width="20"> Shows all *Following* from Twitch including who you have unfollowed
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/f4f_menu.png" height="20" width="20"> Overview & simplification users who *F4F (Follow for Follow)*
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/vod_menu.png" height="20" width="20"> Overview for VODs (Videos On Demand) & exporting VODs
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/userinfo.png" height="20" width="20"> Quick overview about your Twitch account
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/info.png" height="20" width="20"> Shows different platforms for further information such as a *User Manual* & *Contact*
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/settings.png" height="20" width="20"> The settings menu is split up into further features below
  + <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/autofollow.png" height="20" width="20"> Settings for activating & configuring the AutoFollow Service
  + <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/vod_menu.png" height="20" width="20"> Settings for activating & configuring the AutoVODExport Service
  + <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/authorization.png" height="20" width="20"> Logs out of StreamingYorkie & Twitch as well as deleting any data relating to your Twitch account

#### Categories
Each Menu is split into 3-4 of the following categories:
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/new_button.png" height="20" width="20"> All new Followers/Following
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/follow.png" height="20" width="20"> All current Followers/Following (including New)
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/unfollow.png" height="20" width="20"> All Followers who Unfollowed you or Following you Unfollowed
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/excluded.png" height="20" width="20"> Users that are excluded from AutoFollow as well as being excluded from the other menus
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/notfollowing_followers.png" height="20" width="20"> Users who Follow you, but you dont Follow them back
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/follow4follow.png" height="20" width="20"> Followers who also Follow you
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_nonfollowers.png" height="20" width="20"> Users you Follow, who dont Follow you back
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/vods.png" height="20" width="20"> VODs (Videos On Demand) that are available on your Twitch account
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/export.png" height="20" width="20"> Exported VODs done by StreamingYorkie will be found here

#### Actions
Each category contains up to 3 of the following actions:
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/follow.png" height="20" width="20"> Follows the chosen User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/unfollow.png" height="20" width="20"> Unfollows the chosen User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/notifications.png" height="20" width="20"> Activates notifications received from the User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/deactivate_notifications.png" height="20" width="20"> Deactivates notifications received from the User
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/excluded.png" height="20" width="20"> Excludes Users from other categories & from AutoFollow
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/include.png" height="20" width="20"> Includes Users from other categories & from AutoFollow
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/delete.png" height="20" width="20"> Deletes User from the Device (all users, even the unfollowed ones are saved until otherwise)
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/export.png" height="20" width="20"> Exports a VOD from Twitch to Youtube
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/cancel.png" height="20" width="20"> Cancels the current action being taken
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/refresh.png" height="20" width="20"> Refreshes the current view (if internet is available, a request for new data is sent to Twitch)
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/authorization.png" height="20" width="20"> Logs out of StreamingYorkie & Twitch as well as deleting any data relating to your Twitch account
+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/save.png" height="20" width="20"> Saves the current changes (if any have been made)

---

### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/followers_menu.png" height="30" width="30">Followers, <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_menu.png" height="30" width="30">Following & <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/f4f_menu.png" height="30" width="30">F4F

Here you can see who you follow (a.k.a. *Following*), who follows you (a.k.a. *Followers*), who unfollowed you & an excluded list from the AutoFollow.

To *refresh* the view & send a new request press the refresh button <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/refresh.png" height="20" width="20">

> Each Menu has its own exclusion list for view customization that do **not** reflect in other lists.
>
> Once excluded from any **single** list, it will be excluded from *AutoFollow*.
>
> **_Example:_** Excluding a user in *Follower* will **not** exclude the user in *F4F*, but **will** exclude it from *AutoFollow*.

> In F4F at the end of <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/notfollowing_followers.png" height="20" width="20"> or <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/following_nonfollowers.png" height="20" width="20"> there is an option to **Follow/Unfollow all users** within the category.

---

### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/vod_menu.png" height="30" width="30"> VODs 

Here VODs *(Videos On Demand)* that are currently available from your profile on Twitch.
The title, game & creation date will be displayed with a preview image of the VOD as well.

All VODs can be exported once & will not heavily affect network usage.
The exportation of the VOD is done by Twitch to your linked Youtube account. As it is done per the Twitch website.

A dialog will appear to edit any information such as: 
+ **Title:** The title of the VOD once exported
+ **Description:** VOD description as per Youtube standards
+ **Tags:** List of Tags as per Youtube standards
+ **Visibility:** If the VOD should be private (off) or public (on)
+ **Split:** If the VOD should be split into 15 minute segments

Exported VODs will be visible under the *Exported* menu category.
VODs that are unavailable on Twitch & have been exported can be deleted.

To *refresh* the view & send a new request press the refresh button <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/refresh.png" height="20" width="20">

---

### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/userinfo.png" height="30" width="30"> User Info 

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

### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/info.png" height="30" width="30"> Info

Here you can find external links in relation to the Streaming Yorkie & its developer.
 
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

### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/settings.png" height="30" width="30"> Settings

#### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/f4f_menu.png" height="30" width="30"> Autofollow

Here you can configure the *AutoFollow* to run in the background.

+ **AutoFollow Service:** *Following, Unfollowing or Following & Unfollowing* can be activated.

+ **Interval:** The interval slider ranges from *1-60* with a unit option of *Minutes, Hours or Days*.
Please be wary, due to a high battery consumption & inefficiency from other Apps, the interval has been restricted by Android to a minimum of 15 Minutes. Going below this will default back to 15 Minutes.

+ **Enable Notifications:** You can switch on or off whether the *AutoFollow* activates notifications for each new Follower.

+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/save.png" height="20" width="20"> **Save:** Once you are done and changes have been made, it will be saved locally as a file.

> **Warning**
>
> Please make sure you have excluded Followers & Following you wish to be left alone from the AutoFollow Service.

If you run into problems with the AutoFollow Service, deactivate it & [contact](#contact) me.

#### <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/vod_menu.png" height="30" width="30"> AutoVODExport

Here you can configure the *AutoVODExport* to run in the background.

+ **AutoVODExport Service:** Select *Export* to be activate.

+ **Interval:** The interval slider ranges from *1-60* with a unit option of *Hours or Days*.
We recommend you not to do it too often as it will affect unnecessary battery & network consumption. Once a week is plenty.

+ **Visibility:** If the VODs should have private or public visibility on Youtube

+ **Split:** If the VODs should be split into 15 Min segments

+ <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/save.png" height="20" width="20"> **Save:** Once you are done and changes have been made, it will be saved locally as a file.

---

## Updates

### 1.1.1-a

Patch for fixing issues below

Features:
+   VOD Overview
+   VOD Export
+   VOD Export Automation

Issues:
+	F4F Settings fix
+   WriteFile append with line break
+	VOD Export while streaming fix
+	VOD count and removal when offline / non-existent

---

### 1.1.0-a

First minor release for new features.

Features:
+   VOD Overview
+   VOD Export
+   VOD Export Automation

Issues:
+	Request & file writing synchronization
+   Login/Logout correction fix
+	Encoding issues fix (UTF-8)
+	Action button issue fix

Other:
+   Styling updated
+   Logout option in Settings
+   JUnit tests for package:file
+	DRY Principle enforcements improved
+   New screenshots for Google Play

---

### 1.0.4-a

Patch for fixes listed below.

Issues:
+	Follow/Unfollow all action implementation (was missing after recycler view)
+	Updates for Google Play Policies (code was not pushed)

---

### 1.0.3-a

Patch for fixes listed below.

Issues:
+	F4F Menu Users movement correction
+	Updates for Google Play Listing Policies

---

### 1.0.2-a

Patch for fixes listed below.

Issues:
+	Link to Guide/Manual in Info
+	User list order wrong (due to Collections reverse order mistake)
+	Weak References checked against null
+	Nested weights in info.xml removed (redesigned)
+	F4F Menu Users removed from view when Follow/Unfollow action taken
+	Check in place for Follow/Unfollow/Notification actions when offline
+	Bug/Issue template for Github & replaced in readme
+	Implemented Javadoc Checkstyle restrictions
+	Implemented Lint restrictions (for maintaining high code quality)

---

### 1.0.1-a

Patch for fixes listed below.

Issues:
+	User list order wrong (previous code was not implemented)
+	Activity refreshes on screen rotation fix
+	Exclusion lists inconsistency fix (wrong path & decision to keep)

---

### 1.0.0-a

First Public Major Release.

Features:
+	User Overview
+	Follower, Following & Follow4Follow Overview
+	Developer Overview
+	Settings implemented
+	AutoFollow implemented
+	Login/Logout implemented

Issues:
+	User list view lag
+	User list order wrong
+	Action buttons functionality fix
+	AutoFollow activation fix
+	Multiple request collision fix
+	Skipped frames fix

---

## Roadmap

The following tasks and features are currently on the roadmap & some of which are likely to be within the next release.

+ Unit tests until 100% code coverage
+ Data backup & recovery
+ Extend Settings for general (Wifi only, user pic size, theme choice)
+ Stream Scheduler (for past, current & future streams)
+ Quick Tap host
+ Offline App Guide
+ Host for Host
+ Follow 4 Follow Group (in App or link to Discord)
+ Status, Game, Tags & Description editing option 
+ Reusable Activities (Go Live text, game, tags)
+ Instant AutoFollow & AutoUnfollow (based on webhooks)
+ Stream Info/Event tracker (views, hosts, followers, chats)
+ Stream Lurker (audio only mode)
+ Streaming tips (obs settings, camera & green screen, networking, chat interactions)
+ Handle Multiple/Dual accounts (eg. one for following, one for followers)
+ i18n (Internationalization)
+ Follower/Following search

---

## Issues

Please report any issues you may come across to help improve Streaming Yorkie.
You can either use [Githubs Issue Tab](https://github.com/LethalMaus/StreamingYorkie/issues/new) or contact me on [Discord](https://discord.gg/asZsz2F).
Please try to use the following template to help resolve the issue quicker.

```
**Description:**

**Reproduction:**

**Code Path:**

**StreamingYorkie Version:**

**Android Version:**

**Smartphone Make & Model:**

```

> Description: A description of what the issue is
>
> (Bug/Problem) Reproduction: How to reproduce the error or proof (picture/video) of the error
>
> (Code Inefficiency) Code Path: Where to find the code inefficiency e.g. Link to class & line number
>
> Streaming Yorkie Version: Version of App being used e.g. 1.0.0-a
>
> Android Version: Version of Android being used e.g 4.4.4
>
> Smartphone Make & Model: Where the App is being used e.g Samsung S9

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

I'm on [LinkedIn](https://www.linkedin.com/in/james-cullimore-042ab397/). Here you will find my development preferences & experiences.
