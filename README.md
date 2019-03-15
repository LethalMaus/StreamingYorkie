# StreamingYorkie

<p align="center">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/streaming_yorkie-web.png"><br><br>
</p>

---

## Table of Contents

+ [Introduction](#introduction)
+ [Guide](#guide)
  + [Login & Logout](#login-logout)
  + [Followers, Following & F4F](#followers-following-f4f)
    + [Menu Categories](#menu-categories)
    + [Catgory Actions](#category-actions)

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
**DRY** (Dont Repeat Yourself) & **KISS** (Keep It Simple Stupid) principles are enforced as much as possible. Variable naming conventions are in place. 

> The Variable name must explain what it is or what it does, clearly, for any developer to understand.

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

If you would like anything else to be displayed, or have the info display differently, [let me know](#contact).

---

### Info

Here you can find external links in relation to the Streaming Yorkie & its Developer.
 
Source Code & Documentation

<a href="https://github.com/LethalMaus/StreamingYorkie">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/github.png" height="60">
</a><br>

Support & Descriptive Animals Community

<a href="https://discord.gg/asZsz2F">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/discord.png" height="60"> 
</a><br>

Live Streams & Entertainment

<a href="https://www.twitch.tv/lethalmaus">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/twitch.png" height="60">
</a><br>

Membership & Partnership

<a href="https://www.patreon.com/LethalMaus/creators">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/patreon.png" height="60">
</a><br>

Donations (not expected, but welcomed)

<a href="https://paypal.me/JamesCullimore/2,50">
  <img src="https://github.com/LethalMaus/StreamingYorkie/blob/master/streaming_yorkie/src/main/res/drawable/paypal.png" height="60">
</a><br>

#### Logs
Tapping the *Developer Logo* **8** times will give you access to the app logs & files. An error log is available along with much more for support.

---
