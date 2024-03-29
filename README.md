# StreamingYorkie

<p align="center">
  <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/streaming_yorkie-web.png"><br>
  <b>A Twitch Streamers Best Friend</b>
</p>

---

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=alert_status)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=security_rating)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=bugs)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=ncloc)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=LethalMaus_StreamingYorkie&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie)

## Table of Contents

+ [Introduction](#introduction)
+ [Contact](#contact)
+ [Shout-outs](#shout-outs)
+ [Guide](#guide)
+ [Updates](#updates)
+ [Release Prerequisites](#release-prerequisites)
+ [Roadmap](#roadmap)
+ [Issues](#issues)

---

## Introduction

Streaming Yorkie is a tool designed to help *Twitch streamers* to efficiently *follow & unfollow* other streamers.
The tool tracks & offers a better overview of *followers, following, unfollowed & F4F*.
After your stream any *VOD (Video On Demand)* can be seen & exported to *Youtube* in our VODs Overview.
**Automatically follow, unfollow, F4F, lurk & export VODs to Youtube** simply by installing and configuring Streaming Yorkie.
Share that you are **AutoFollowing** to our Discord community & gain more followers.
Helps build a bigger community, simplifies/automates communication, become an *affiliate* or *partner* faster & free up time to stream more.
Watch multiple streams at once with **Multi View** and cast it to a TV.
**Lurk** your favourite streamers with minimum possible network data usage (audio only & chat) & increase their viewer count.
Increase channel points & chances of drops or gifted subs.
Add, remove and re-prioritize hosting streamers on your AutoHost list. 
Use our Discord to get others to to follow or lurk and gain more viewers. Use the F4F-L4L-H4H Channel.

<p align="center">
    <a href='https://play.google.com/store/apps/details?id=com.lethalmaus.streaming_yorkie&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>
</p>

Currently it is in an **Alpha** state.
It could be that a *Bug* may appear or the app behaves in an unexpected manner.
Please report any problems, suspicions or confirmations to help improve Streaming Yorkie for everyone.

The code is **Open Source**, contains **no costs & no advertisements** are involved.
It is a gift back to a great community who I will continue to support for **free**.
The option to support us and gain extra benefits such as Follows or Lurks is available as in-app purchases and subscriptions
 
Streaming Yorkie communicates exclusively with the [Twitch API](https://dev.twitch.tv/), [Twitch Website](https://twitch.tv), [Twitch Multi View](https://github.com/LethalMaus/TwitchMultiView) & our [Discord](https://discord.gg/vkCHjVm)
A login is required to be able to utilize your Twitch data.
Passwords are not seen or saved, rather a unique Token given by Twitch is used after the login.
This ensures that your account is as safe as can be.
All data is saved locally & only you have access to it.
VOD exports are done by Twitch, they are not downloaded & uploaded to Youtube by Streaming Yorkie.
This ensures network data usage is kept to a minimum.

Streaming Yorkie is developed with *Android Studio* in *Java*.
*Lint* & [Sonarcloud](https://sonarcloud.io/dashboard?id=LethalMaus_StreamingYorkie) is used to acquire high quality standardized code with as little complexity possible.
The code is documented with *JavaDocs* & comments to allow for easier collaborations with other developers. JavaDocs are enforced & without them the build fails
**DRY** *(Don't Repeat Yourself)* & **KISS** *(Keep It Simple Stupid)* principles are enforced as much as possible.
Variable naming conventions are also in place.

> The variable name must explain what it is or what it does, clearly, for any developer to understand.

Hopefully this can be used as an example for good coding as well as how to develop an Android App in Java.
The code includes examples of:

+ RecyclerView, Adapter, Drag & Drop
+ Room, SQLite, DAO & Entity
+ Volley library for HTTP requests
+ Glide library for image rendering
+ Lottie animations & UX design
+ Notifications, Window Manager
+ Foreground Service, Background Service
+ Worker, Async Task, AlarmManager
+ Network Usage Monitor, Broadcast Receiver
+ Thread, Handler, Runnable
+ Programmatic permissions, Mira casting
+ In-app purchases & subscriptions
+ File read, write & delete operations
+ Custom views, Scroll View, ...
+ Constraint, Relative & Linear Layouts
+ WebView, TextView, ImageView, ...
+ Activity, Intent, Listener, ...
+ Gradle, Build relies on, 
+ Checkstyle, JavaDocs, Lint & Sonar
+ Productive deployments to Google Play Store

Any collaborations are welcome, so feel free to fork & ask for merge requests.
See something inefficient? [Let me know](#contact). I'm always trying to improve my coding & learn new things.

---

## Contact

Email: [DescriptiveAnimals@gmail.com](mailto:DescriptiveAnimals@gmail.com?subject=[GitHub]%20StreamingYorkie)

For any issues, questions or change requests, feel free to ask on [Discord](https://discord.gg/vkCHjVm). I will get back to you as soon as I can.

Become a [Patron](https://www.patreon.com/LethalMaus). Help decide what my main focus should be & get extra benefits.

Any [support or donations](https://paypal.me/JamesCullimore/2,50) are highly appreciated (but not expected) & go towards improving development & entertainment.

I also love to entertain, game & stream as a hobby on [Twitch](https://www.twitch.tv/lethalmaus). If you're interested, come & say 'Hi'.

I'm also on [LinkedIn](https://www.linkedin.com/in/james-cullimore-042ab397/). Here you will find my development preferences & experiences.

Click on one of the images below to be redirected a designated site.

<p align="center">
  <a href="https://discord.gg/vkCHjVm"><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/discord.webp" height="60"></a>
  <a href="https://www.twitch.tv/lethalmaus"><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/twitch.webp" height="60"></a>
  <a href="https://www.patreon.com/LethalMaus"><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/patreon.webp" height="60"></a>
  <a href="https://paypal.me/JamesCullimore/2,50"><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/paypal.webp" height="60"></a>
</p>

---

## Shout-outs

A big thank you to you all for going out of your way supporting Streaming Yorkie. Whether it's bug reporting, feature requests, promoting or in-app purchases, your contribution is highly appreciated.

<table>
  <tr>
    <td align="center"><a href="https://www.twitch.tv/werwuermchen">Werwuermchen</a></td>
    <td align="center"><a href="https://www.twitch.tv/biaxxation">biaxxation</a></td>
    <td align="center"><a href="https://www.twitch.tv/techno17ad">techno17ad</a></td>
    <td align="center"><a href="https://www.twitch.tv/smokeywoodz">SMOKEYWOODZ</a></td>
    <td align="center"><a href="https://www.twitch.tv/rhexisgaming">Rhexisgaming</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://www.twitch.tv/killmonka1997">KILLMONKA1997</a></td>
    <td align="center"><a href="https://www.twitch.tv/gamingwithak47">gamingwithak47</a></td>
    <td align="center"><a href="https://www.twitch.tv/graspingguiter01">graspingguiter01</a></td>
    <td align="center"><a href="https://www.twitch.tv/grandadtree">GrandadTree</a></td>
    <td align="center"><a href="https://www.twitch.tv/chicken_tendies">chicken_tendies</a></td>
  </tr>
  <tr>
      <td align="center"><a href="https://www.twitch.tv/dmb2k">dmb2k</a></td>
      <td align="center"><a href="https://www.twitch.tv/tg__assassin">TG__ASSASSIN</a></td>
      <td align="center"><a href="https://www.twitch.tv/elsize29">elsize29</a></td>
  </tr>
</table>

---

## Guide

+ [Login & Logout](#-login--logout)
+ [Symbols & Icons](#symbols--icons)
  + [Menu](#menu)
  + [Categories](#categories)
  + [Actions](#actions)
+ [Followers, Following & F4F](#followers-following--f4f)
+ [VODs](#-vods)
+ [MultiView](#-multiview)
+ [Lurk](#-lurk)
+ [Host](#-host)
+ [User Info](#-user-info)
+ [Info](#-info)
  + [Logs](#logs)
+ [Shop](#-shop)
+ [Settings](#-settings)
  + [AutoFollow](#-autofollow)
  + [AutoVODExport](#-autovodexport)
  + [AutoLurk](#-autolurk)

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/authorization.webp" height="30" width="30"> Login & Logout

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
<details>
<summary>Several menus are available to divide StreamingYorkie into each feature it offers:</summary>
<ul>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/followers_menu.webp" height="20" width="20"> Shows all <i>Followers</i> from Twitch including who has unfollowed</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/following_menu.webp" height="20" width="20"> Shows all <i>Following</i> from Twitch including who you have unfollowed</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/f4f_menu.webp" height="20" width="20"> Overview & simplification users who <i>F4F (Follow for Follow)</i></li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/vod_menu.webp" height="20" width="20"> Overview for VODs (Videos On Demand) & exporting VODs</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/multi_menu.webp" height="20" width="20"> Watch multiple streams at once on one screen</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/lurk_menu.webp" height="20" width="20"> Lurk streamers and add to their view count</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/host_menu.webp" height="20" width="20"> Adding removing & re-prioritizing hosting streamers in your AutoHost list</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/userinfo.webp" height="20" width="20"> Quick overview about your Twitch account</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/info.webp" height="20" width="20"> Shows different platforms for further information such as a <i>Twitch</i> & <i>Contact</i></li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/shop.webp" height="20" width="20"> Shop for in-app purchase and subscriptions</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/guide.webp" height="20" width="20"> Github link to ReadMe guide</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/help.webp" height="20" width="20"> Offline help guide</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/update.webp" height="20" width="20"> Github link to Updates</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/sourcecode.webp" height="20" width="20"> Github link to the source code</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/contact.webp" height="20" width="20"> Github link to Contact</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/discord.webp" height="20" width="20"> Link to Discord</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/twitch.webp" height="20" width="20"> Link to developers stream</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/patreon.webp" height="20" width="20"> Membership</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/github.webp" height="20" width="20"> Projects</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/settings.webp" height="20" width="20"> The settings menu is split up into further features below</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/f4f_menu.webp" height="20" width="20"> Settings for activating & configuring the AutoFollow Worker</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/vod_menu.webp" height="20" width="20"> Settings for activating & configuring the AutoVODExport Worker</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/lurk_menu.webp" height="20" width="20"> Settings for activating & configuring the AutoLurk Worker</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/authorization.webp" height="20" width="20"> Logs out of StreamingYorkie & Twitch as well as deleting any data relating to your Twitch account</li>
</ul>
</details>

#### Categories
<details>
<summary>Each Menu is split into 3-4 of the following categories:</summary>
<ul>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/followed.webp" height="20" width="20"> All current Followers/Following (including New)</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unfollowed.webp" height="20" width="20"> All Followers who Unfollowed you or Following you Unfollowed</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/excluded.webp" height="20" width="20"> Users/VODs that are excluded from AutoFollow/AutoExport as well as being excluded from the other menus</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/notfollowing_followers.webp" height="20" width="20"> Users who Follow you, but you dont Follow them back</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/follow4follow.webp" height="20" width="20"> Followers who also Follow you</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/following_nonfollowers.webp" height="20" width="20"> Users you Follow, who dont Follow you back</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/vods.webp" height="20" width="20"> VODs (Videos On Demand) that are available on your Twitch account</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/export.webp" height="20" width="20"> Exported VODs done by StreamingYorkie will be found here</li>
</ul>
</details>

#### Actions
<details>
<summary>Each category contains up to 3 of the following actions:</summary>
<ul>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/followed.webp" height="20" width="20"> Currently following & unfollows the chosen user when pressed</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unfollowed.webp" height="20" width="20"> Currently not following & follows the chosen user when pressed</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/notifications_activated.webp" height="20" width="20"> Notifications are active, pressing deactivates it</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/notifications_deactivated.webp" height="20" width="20"> Notifications are not active, pressing activates it</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/included.webp" height="20" width="20"> Currently included & excludes Users/VODs from other categories & from AutoFollow</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/excluded.webp" height="20" width="20"> Currently excluded & includes Users/VODs from other categories & from AutoFollow</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/delete.webp" height="20" width="20"> Deletes User/VOD from the Device (not Twitch)</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/export.webp" height="20" width="20"> Exports a VOD from Twitch to Youtube</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/multi.webp" height="20" width="20"> Start watching multiple streams at once on one screen</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/lurk.webp" height="20" width="20"> Initiate lurking given streamer</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unlurk.webp" height="20" width="20"> Stop lurking chosen streamer</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/host.webp" height="20" width="20"> Adds a streamer to the AutoHost list</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unhost.webp" height="20" width="20"> Removes a streamer from the AutoHost list</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/move.webp" height="20" width="20"> Moves a streamer to a different priority ranking</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/message.webp" height="20" width="20"> Opens a dialog to send a message to the chosen channels chat</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/send.webp" height="20" width="20"> Sends the message to channel chat</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/buy.webp" height="20" width="20"> Starts the purchase flow for the respective item</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/cancel.webp" height="20" width="20"> Cancels the current action being taken</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/update.webp" height="20" width="20"> Refreshes the current view (if internet is available, a request for new data is sent to Twitch)</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/authorization.webp" height="20" width="20"> Logs out of StreamingYorkie & Twitch as well as deleting any data relating to your Twitch account</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/save.webp" height="20" width="20"> Saves the current changes (if any have been made)</li>
<li><img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/error.webp" height="20" width="20"> Signals that the purchase is not possible</li>
</ul>
</details>

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/followers_menu.webp" height="30" width="30">Followers, <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/following_menu.webp" height="30" width="30">Following & <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/f4f_menu.webp" height="30" width="30">F4F

Here you can see who you follow (a.k.a. *Following*), who follows you (a.k.a. *Followers*), who unfollowed you & an excluded list from the AutoFollow.

New followers/following will be marked with a <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/new_button.webp" height="20" width="20"> on the top right of their logo

Click on the user logo to be redirected to their profile on Twitch.

The data can/will be refreshed on entering a menu, or changing a category

An action-all button will appear on the bottom in cases where all user actions are the same.

> **_Example:_** In F4F at the end of <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/notfollowing_followers.webp" height="20" width="20"> or <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/following_nonfollowers.webp" height="20" width="20"> there is an option to **Follow/Unfollow all users** within the category.

Each Menu has its own exclusion list for view customization that do **not** reflect in other lists.

Once excluded from any **single** list, it will be excluded from *AutoFollow*.

> **_Example:_** Excluding a user in *Follower* will **not** exclude the user in *F4F*, but **will** exclude it from *AutoFollow*.

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/vod_menu.webp" height="30" width="30"> VODs

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

To *refresh* the view & send a new request press the refresh button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/update.webp" height="20" width="20">

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/multi_menu.webp" height="30" width="30"> MultiView

Here you can watch multiple streams at once on one screen.

Enter up to 4 channel names that are currently online and press the MultiView start button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/multi.webp" height="20" width="20">.
The view has been optimized to fit any screen and utilize as much space as it can.

A cast button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/cast.webp" height="20" width="20"> directs to the Mirror Cast settings on any device.

The MultiView uses an external website [TwitchMultiView](https://lethalmaus.github.io/TwitchMultiView?channels=lethalmaus), designed and hosted by the same developer.

The soft keys can be seen either by swiping from the bottom up, or from the top down.

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/lurk_menu.webp" height="30" width="30"> Lurk

Here you can lurk any live stream and add to their viewer count. Lurking helps other channels get higher on list to increase the chances of more viewers, followers, etc.

Enter the channel name and to start lurking press the lurk button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/lurk.webp" height="20" width="20">.
Channels that are being lurked will be shown in a table below the input bar.
The channel picture will only be displayed if you are also currently following the channel.

When a channel is being lurked, you will have the option to write to chat.
Pressing the message button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/message.webp" height="20" width="20"> will open a dialog
In the dialog, enter the message you would like to be sent then press the send button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/send.webp" height="20" width="20">
This creates a temporary Bot with your name, sends the message & disconnects.

To stop lurking a channel press the unlurk button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unlurk.webp" height="20" width="20">.
This will also stop it form being lurked automatically with AutoLurk.

To include it in the AutoLurk, press the Lurk button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unlurk.webp" height="20" width="20"> when the user is offline.

To remove a channel from the list press the delete button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/delete.webp" height="20" width="20">.

Lurking starts a separate service which requires the **Overlay Permission**.
An invisible browser is shown in the Overlay to make the browser think you are watching the stream (as a viewer would).
The videos within the browser only pull & play the audio on the minimal possible volume to reduce the network load.
Only this way along with being connected to chat, can you be counted as a viewer.
For each channel lurked, a Bot with your name is created and added to the channel

The service will display a notification saying how many channels are being lurked and the apps **total** network usage.
The network usage includes other requests within the app irrelevant from lurking.
The service can be paused, stopped & started from the notification.
The notification only disappears once the service has ended.

> Using the Twitch App will disable the lurk service. Use MultiView or another device to watch while lurking.

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/host_menu.webp" height="30" width="30"> Host

Here you can add, remove and re-prioritize hosting streamers on your AutoHost list.

Enter a channel name, then click the host button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/host.webp" height="20" width="20">.
If the user exists, they will be added at the bottom of the AutoHost list.

Click <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/unhost.webp" height="20" width="20"> to remove them from the list.

By holding <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/move.webp" height="20" width="20"> or the view in general, you can give the user a higher/lower priority in the list.

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/userinfo.webp" height="30" width="30"> User Info

This is an overview of the *User* who is currently logged in, that shows the following:
+ **Logo:** Your profile picture
+ **Username:** Your login name / username
+ **ID:** Your unique Twitch ID
+ **Game:** The current game you are playing
+ **Member Since:** When you joined
+ **Views:** How many views you have
+ **Followers:** How many followers you have
+ **Broadcaster Type:** If you are affiliated or partnered
+ **Status:** Your Go Live status
+ **Description:** Your profile description

Not all information can be shown unless 2FA (Two Factor Authentication) is activated and the user has streamed before.

To *refresh* the view & send a new request press the refresh button <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/update.webp" height="20" width="20">

If you would like anything else to be displayed, or have the info display differently, [let me know](#contact).

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/info.webp" height="30" width="30"> Info

Here you can find external links, offline guides and more in relation to the Streaming Yorkie & its developer:
 
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/guide.webp" height="20" width="20"> Github link to ReadMe guide
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/help.webp" height="20" width="20"> Offline help guide
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/update.webp" height="20" width="20"> Github link to Updates
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/sourcecode.webp" height="20" width="20"> Github link to the source code
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/contact.webp" height="20" width="20"> Github link to Contact
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/twitch.webp" height="20" width="20"> Link to developers stream
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/patreon.webp" height="20" width="20"> Membership
+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/github.webp" height="20" width="20"> Projects

On the bottom of the screen, the current app version should be shown.

#### Logs

Tapping the *Developer Logo* **8** times will give you access to the app logs & files. An error log is available along with much more for support purposes.

> **Warning**
>
> We cannot gain or have access to these files due to security reasons.

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/shop.webp" height="30" width="30"> Shop

Here you can support Streaming Yorkie with either in-app purchases or subscriptions.

Some items can be purchased multiple times. Only one subscription can be active at one given time.

To purchase an item, click on <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/buy.webp" height="20" width="20">

If an item is not available for purchase, it will be displayed with <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/error.webp" height="20" width="20">

Thank you for any purchases made and your continuous support regardless.

---

### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/settings.webp" height="30" width="30"> Settings

#### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/f4f_menu.webp" height="30" width="30"> Autofollow

Here you can configure the *AutoFollow* to run in the background.

+ **AutoFollow Service:** *Following, Unfollowing or Following & Unfollowing* can be activated.

+ **Interval:** The interval slider ranges from *1-60* with a unit option of *Minutes, Hours or Days*.
Please be wary, due to a high battery consumption & inefficiency from other Apps, the interval has been restricted by Android to a minimum of 15 Minutes. Going below this will default back to 15 Minutes.

+ **Enable Notifications:** You can switch on or off whether the *AutoFollow* activates notifications for each new Follower.

+ **Share F4F Status:** You can share that you are AutoFollowing to our Discord channel for others to know that when they follow you, they get followed back.

+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/save.webp" height="20" width="20"> **Save:** Once you are done and changes have been made, it will be saved locally as a file.

> **Warning**
>
> Please make sure you have excluded Followers & Following you wish to be left alone from the AutoFollow Service.
>
> Be wary when following/unfollowing when AutoFollow is active, new users will have to handled accordingly

If you run into problems with the AutoFollow Service, deactivate it & [contact](#contact) me.

#### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/vod_menu.webp" height="30" width="30"> AutoVODExport

Here you can configure the *AutoVODExport* to run in the background.

+ **AutoVODExport Service:** Select *Export* to be active.

+ **Interval:** The interval slider ranges from *1-60* with a unit option of *Hours or Days*.
We recommend you not to do it too often as it will affect unnecessary battery & network consumption. Once a week is plenty.

+ **Visibility:** If the VODs should have private or public visibility on Youtube

+ **Split:** If the VODs should be split into 15 Min segments

+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/save.webp" height="20" width="20"> **Save:** Once you are done and changes have been made, it will be saved locally as a file.

#### <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/lurk_menu.webp" height="30" width="30"> AutoLurk

Here you can configure the *AutoLurk* to run in the background and Lurk streamers when they come online.
This can take up to 15 minutes to activate.

+ **Activate AutoLurk:** Switch on to activate.

+ **Lurk on WiFi only:** The service only activates or runs when the device is connected to a WiFi

+ **Inform channel of Lurk:** Sends a message to the streamers chat once the worker has activated

+ **Message:** The message to be sent to the chat, if empty it defaults to '!lurk'

+ <img src="https://lethalmaus.github.io/StreamingYorkie/streaming_yorkie/src/main/res/drawable/save.webp" height="20" width="20"> **Save:** Once you are done and changes have been made, it will be saved locally as a file.

---

## Updates

<details>
<summary>v2.x.x</summary>

+ [2.3.8-a](#238-a)
+ [2.3.7-a](#237-a)
+ [2.3.6-a](#236-a)
+ [2.3.5-a](#235-a)
+ [2.3.4-a](#234-a)
+ [2.3.3-a](#233-a)
+ [2.3.2-a](#232-a)
+ [2.3.1-a](#231-a)
+ [2.3.0-a](#230-a)
+ [2.2.2-a](#222-a)
+ [2.2.1-a](#221-a)
+ [2.2.0-a](#220-a)
+ [2.1.9-a](#219-a)
+ [2.1.8-a](#218-a)
+ [2.1.7-a](#217-a)
+ [2.1.6-a](#216-a)
+ [2.1.5-a](#215-a)
+ [2.1.4-a](#214-a)
+ [2.1.3-a](#213-a)
+ [2.1.2-a](#212-a)
+ [2.1.1-a](#211-a)
+ [2.1.0-a](#210-a)
+ [2.0.3-a](#203-a)
+ [2.0.2-a](#202-a)
+ [2.0.1-a](#201-a)
+ [2.0.0-a](#200-a)

---

### 2.3.8-a

<details>
<summary>Minor bug fixes</summary>

Issue fixed:
+   Workaround for Twitch v5 API cursor bug
+   Fixed Following notification bug

</details>

---

### 2.3.7-a

<details>
<summary>Twitch API Workaround</summary>

Issue fixed:
+   Twitch v5 API is broken, utilising now both v5 & helix for follower info

</details>

---

### 2.3.6-a

<details>
<summary>Bug fix</summary>

Issue fixed:
+   Lurk feature fix due to Twitch API change.

</details>

---

### 2.3.5-a

<details>
<summary>Bug fix</summary>

Issue fixed:
+   Lurk settings activation switch

</details>

---

### 2.3.4-a

<details>
<summary>Hotfix</summary>

Issues fixed:
+   Following bug from twitch api

</details>

---

### 2.3.3-a

<details>
<summary>Hotfix</summary>

Issues fixed:
+   DB crashes

</details>

---

### 2.3.2-a

<details>
<summary>Hotfix</summary>

Issues fixed:
+   Follower/following bug

</details>

---

### 2.3.1-a

<details>
<summary>Bug fixes & minor improvements</summary>

Improvements:
+   Deep linking for easier following on discord

Issues fixed:
+   Follower/following bug

</details>

---

### 2.3.0-a

<details>
<summary>AutoHost List & bug fixes</summary>

Features:
+   AutoHost list
+   Additional lurk settings

Improvements:
+   Sonarcloud lint fixes
+   Complexity reduction
+   Changed close icon in notifications bar

Issues fixed:
+   Added cursor to followers request to avoid 1600 limit
+   Pressing back and navigating correctly to the dashboard
+   Removed support for Android < 6.0
+   Removed Google Play connectivity toast

</details>

---

### 2.2.2-a

<details>
<summary>Lottie animations & UI/UX improvements</summary>

Improvements:
+   Lottie animations & UI/UX
+   Shout-outs link

Issues fixed:
+   Login issue on older devices fixed
+   VOD export issue due to Twitch changes fixed
+   Lurking issue due to Twitch changes fixed
+   Toasts outside of app fix
</details>

---

### 2.2.1-a

<details>
<summary>Worker Hotfix</summary>

Issues fixed:
+   Worker crash on start fix
</details>

---

### 2.2.0-a

<details>
<summary>New features, improvements & fixes</summary>

Features:
+   Shop for in-app purchases & subscriptions
+   User logo redirects to Twitch

Improvements:
+   Complexity reduction
+   Lambda replacements
+   Removed irrelevant test classes
+   Defined constants

Issues fixed:
+   New followers not showing in list fix
+   Login process not following defined flow fix
</details>

---

### 2.1.9-a

<details>
<summary>Improvements & Fixes</summary>

Improvements:
+   Lurk - Ensuring the lurk counts as a view

Issues fixed:
+   Login - adapting to changes in Twitch login procedure
</details>

---

### 2.1.8-a

<details>
<summary>Lurk Hot Fix</summary>

Issues fixed:
+   Lurk - RemoteServiceException fix
</details>

---

### 2.1.7-a

<details>
<summary>Lurk Hot Fix</summary>

Issues fixed:
+   Lurk - Handler & Looper fix
</details>

---

### 2.1.6-a

<details>
<summary>Lurk Hot Fix</summary>

Issues fixed:
+   Lurk - Handler & Looper fix
</details>

---

### 2.1.5-a

<details>
<summary>Improvements & Fixes</summary>

Issues fixed:
+   Lurk - Alarm & doze mode
+   Lurk - Error handling

Improvements:
+   Lurk - Sing request for online users
+   Discord - Permanent links
+   Lambda - more Lambda refactoring
</details>

---

### 2.1.4-a

<details>
<summary>Improvements & Fixes</summary>

Issues fixed:
+   Info - Layout alignment fix

Improvements:
+   AutoLurk - AlarmManager API specific calls
+   Lambda - more Lambda refactoring
</details>

---

### 2.1.3-a

<details>
<summary>Hot fix for DB migration</summary>

Issues fixed:
+   Room - Database version upgrade & migration fix due to undefined constant in sql query
</details>

---

### 2.1.2-a

<details>
<summary>Hot fix for DB migration</summary>

Issues fixed:
+   Room - Database version upgrade & migration fix due to undefined constant in sql query
</details>

---

### 2.1.1-a

<details>
<summary>Hot fix for DB migration</summary>

Issues fixed:
+   Room - Database version upgrade & migration fix
</details>

---

### 2.1.0-a

<details>
<summary>New feature & many improvements</summary>

Features:
+   AutoLurk
+   Lurk Messaging with PircBotX

Issues fixed:
+   Lurk - lurk not counted as view fix
+   Adapter - threading fix

Improvements:
+   LurkAdapter - now uses Room
+   Lambda - more Lambda refactoring
+   Sonarcloud - new rules & improvements
+   PNG images converted to WEBP format
+   Discord - improved linking & notifications
+   Security - Random changed to SecureRandom
+   File to Room - removed temporary file export
</details>

---

### 2.0.3-a
 
<details>
<summary>Patch for minor fixes</summary>

Issues fixed:
+   RecyclerView - crash reduction per post method IndexOutOfBoundsException fix
+   Worker - fix activating/deactivating worker

Improvements:
+   Worker - activation/deactivation handling
</details>

---

### 2.0.2-a
 
<details>
<summary>Patch for minor fixes</summary>

Issues fixed:
+   Lurk - Redundant API call replacement fix
+   RecyclerView - crash IndexOutOfBoundsException fix
+   WriteFileHandler - toasts on error only when activity is present fix
+   AutoFollow - creation of notification directories fix
+   UserView - loading logo crash

Other:
+   Gradle update
+   RecyclerView update
+   Room update
</details>

---
 
### 2.0.1-a

<details>
<summary>Patch for minor fixes</summary>

Issues fixed:
+   UserView crash on displaying chanel name fix
+   RecyclerView crash IndexOutOfBoundsException fix
+   FollowRequestHandler NumberFormatException crash fix

Improvements:
+   Request architecture change for faster more efficient http requests
+   SQLite & Room introduction for efficient data management
+   Explicit threading for smoother UI loading and data handling

Other:
+   Gradle update
+   Sonarcloud introduction
+   SDK update
+   Java update
</details>

---

### 2.0.0-a

<details>
<summary>Major update affecting architecture</summary>

Improvements:
+   Request architecture change for faster more efficient http requests
+   SQLite & Room introduction for efficient data management
+   Explicit threading for smoother UI loading and data handling

Other:
+   Gradle update
+   Sonarcloud introduction
+   SDK update
+   Java update
</details>

---

</details>
<details>
<summary>v1.x.x</summary>

+ [1.3.1-a](#131-a)
+ [1.3.0-a](#130-a)
+ [1.2.1-a](#121-a)
+ [1.2.0-a](#120-a)
+ [1.1.3-a](#113-a)
+ [1.1.2-a](#112-a)
+ [1.1.1-a](#111-a)
+ [1.1.0-a](#110-a)
+ [1.0.4-a](#104-a)
+ [1.0.3-a](#103-a)
+ [1.0.2-a](#102-a)
+ [1.0.1-a](#101-a)
+ [1.0.0-a](#100-a)

---

### 1.3.1-a

<details>
<summary>Patch for fixing issues below</summary>

Features:
+   MultiView Cast
+   Lurk Service
+   Share F4F to Discord

Issues fixed:
+	Login Crash due to 2FA
+   Twitch APIv3 Shutdown

Other:
+   Release prerequisites
+   Minor refactoring
+   Improved RequestHandler error handling
+   Improved Navigation handling
</details>

---

### 1.3.0-a

<details>
<summary>Third minor release for new features.</summary>

Features:
+   MultiView Cast
+   Lurk Service
+   Share F4F to Discord

Other:
+   Release prerequisites
+   Minor refactoring
</details>

---

### 1.2.1-a

<details>
<summary>Patch for fixing issues below</summary>

Issues fixed:
+	Empty VOD Crash fix
+   Following toast typo
+   Changed min api to 21 due to WebView restrictions
+   UserView crash fix
+   Autofollow crash fix

Improvements:
+   In-app version display
+   Notification repetition & cancellation 
+   Updated offline icon guide
+   Updated & fixed Github readme
+   Improved play store search
+   VOD preview links
+   Twitch sync message clarification
</details>

---

### 1.2.0-a

<details>
<summary>Second minor release for new features.</summary>

Features:
+   MultiView
+   Guides, Help & Links

Issues fixed:
+	Follow Requests fix
+   User view fix
+	VOD exports during stream
+	Autofollow & UserView crashes fix

Other:
+   Dynamic notification button on follow
+   Reimplemented flag files
</details>

---

### 1.1.3-a

<details>
<summary>Patch for fixing issues below</summary>

Issues fixed:
+	Removed Auth custom timeout
+   Offline User Logo
+   60 Day token refresh
+   Log view line break fix
+   Follow requests not working fix

Improvements:
+   Glide user logo placeholders
+   Error Handling
+   Toasts across App
+   Increased MinSdk to 19
+   Updated Glide Library
+   App icon resolution fix
+   Dynamic F4F objects
</details>

---


### 1.1.2-a

<details>
<summary>Patch for fixing issues below</summary>

Issues fixed:
+	Removed Auth custom timeout
+   Offline User Logo
+   60 Day token refresh
+   Log view line break fix

Improvements:
+   Error Handling
+   Toasts across App
+   Increased MinSdk to 19
+   Updated Glide Library
+   App icon resolution fix
+   Dynamic F4F objects
</details>

---

### 1.1.1-a

<details>
<summary>Patch for fixing issues below</summary>

Features:
+   VOD Overview
+   VOD Export
+   VOD Export Automation

Issues fixed:
+	F4F Settings fix
+   WriteFile append with line break
+	VOD Export while streaming fix
+	VOD count and removal when offline / non-existent
</details>

---

### 1.1.0-a

<details>
<summary>First minor release for new features.</summary>

Features:
+   VOD Overview
+   VOD Export
+   VOD Export Automation

Issues fixed:
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
</details>

---

### 1.0.4-a

<details>
<summary>Patch for fixes listed below.</summary>

Issues fixed:
+	Follow/Unfollow all action implementation (was missing after recycler view)
+	Updates for Google Play Policies (code was not pushed)
</details>

---

### 1.0.3-a

<details>
<summary>Patch for fixes listed below.</summary>

Issues fixed:
+	F4F Menu Users movement correction
+	Updates for Google Play Listing Policies
</details>

---

### 1.0.2-a

<details>
<summary>Patch for fixes listed below.</summary>

Issues fixed:
+	Link to Guide/Manual in Info
+	User list order wrong (due to Collections reverse order mistake)
+	Weak References checked against null
+	Nested weights in info.xml removed (redesigned)
+	F4F Menu Users removed from view when Follow/Unfollow action taken
+	Check in place for Follow/Unfollow/Notification actions when offline
+	Bug/Issue template for Github & replaced in readme
+	Implemented Javadoc Checkstyle restrictions
+	Implemented Lint restrictions (for maintaining high code quality)
</details>

---

### 1.0.1-a

<details>
<summary>Patch for fixes listed below.</summary>

Issues fixed:
+	User list order wrong (previous code was not implemented)
+	Activity refreshes on screen rotation fix
+	Exclusion lists inconsistency fix (wrong path & decision to keep)
</details>

---

### 1.0.0-a

<details>
<summary>First Public Major Release.</summary>

Features:
+	User Overview
+	Follower, Following & Follow4Follow Overview
+	Developer Overview
+	Settings implemented
+	AutoFollow implemented
+	Login/Logout implemented

Issues fixed:
+	User list view lag
+	User list order wrong
+	Action buttons functionality fix
+	AutoFollow activation fix
+	Multiple request collision fix
+	Skipped frames fix
</details>

---

</details>

---

## Roadmap

The following tasks and features are currently on the roadmap & some of which are likely to be within the next release.

+ Stream Info/Event tracker (views, hosts, followers, chats, markers)
+ Report Bug with error log
+ Sonarcloud improvements
+ Pipeline for tests & sonarcloud
+ Use more lottie animations & improve UI/UX
+ Single automated E2E test 
+ Firebase device farm
+ Unit tests until 100% code coverage
+ Extend Settings for general (Wifi only, user pic size, theme choice)
+ Data backup & recovery
+ Stream Scheduler (for past, current & future streams)
+ Quick Tap host
+ Host for Host
+ Clips overview, upload, creation, ... 
+ Status, Game, Tags & Description editing option 
+ Reusable Activities (Go Live text, game, tags)
+ Instant AutoFollow & AutoUnfollow (based on webhooks)
+ Streaming tips (obs settings, camera & green screen, networking, chat interactions)
+ Handle Multiple/Dual accounts (eg. one for following, one for followers)
+ i18n (Internationalization)
+ Follower/Following search

---

## Release Prerequisites

+ Testing
  + Smoke tests on different sdk versions done?
  + JUnit tests defined, changed and executed?
  + Single E2E test changed and executed?
+ Google Play 
  + Listing updated?
  + Screenshots Updated?
+ Github
  + Readme
    + New action? category? menu?
    + Updates extended?
    + New feature documented?
    + Links to Readme sections & pics working?
  + Correct branch?
  + Merged with master?
  + Release with new Tag done?

---

## Issues

Please report any issues you may come across to help improve Streaming Yorkie.
You can either use [Githubs Issue Tab](https://github.com/LethalMaus/StreamingYorkie/issues/new) or contact me on [Discord](https://discord.gg/66EpTMj).
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
