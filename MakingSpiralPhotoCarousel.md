# Making Spiral Photo Carousel #

A fellow consultant Dave Lo and I were recently commissioned to develop a GWT photo carousel based on Doug Greenall’s JavaScript implementation at http://douggreenall.co.uk/site/?p=232. (I have a special place in my heart for Doug as a fellow artificial intelligence major.) In a few days we had developed [this](http://spiral-carousel-gwt.googlecode.com/svn/tags/demo/Spiral.html) version with a few innovations of our own.

In this series of articles I will describe our implementation and how we overcame a number of challenges we faced.

### Laying out the photos ###
The first step for us was laying out the photos. We decided to display a fixed number of photos (8) at a time, rotating through a possibly larger collection of photos. We would achieve the illusion of 3D by making the images in the back smaller and slightly higher than the images in the front. This gives the impression that a circle of images is being viewed from slightly above.

The math for this is fairly simple in theory—just a little bit of trigonometry based on an angle of rotation around the circle and a radius.
```
double x = -Math.sin(angle); // simple x coordinate in unit circle
double y = -Math.cos(angle); // simple y coordinate in unit circle
```
The signs and functions are a bit turned around to accommodate a non-standard coordinate plane that we are using. First of all, the y-axis is flipped to match screen coordinates, which increase as you go down the page. Second we needed the angle to start at zero for the front (bottom) image and increase moving to the left. The resulting system looks like this:

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/coordinate-system.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/coordinate-system.gif)

So all we do is find the angle for each of our images, calculate the x and y coordinates based on the above formula, then flatten out the y axis by scaling it down and shrink or enlarge the images based on their y coordinate. Here’s the breakdown:

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step1.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step1.gif)

**Step 1: Get images coordinates for unit circle.**

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step2.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step2.gif)

**Step 2: Scale X and Y axes.**

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step3.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step3.gif)

**Step 3: Scale images.**

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step4.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/step4.gif)

**Step 4: Move images up and down a bit to achieve spiral look.**

Note that with this last step, we added another image. We now have two in the back-most position, one high and one low.

The code for this can be found in the placeImages() method of the Carousel class (http://code.google.com/p/spiral-carousel-gwt/source/browse/trunk/Carousel/com/reveregroup/carousel/client/Carousel.java).

### Rotating through all the Photos ###
The carousel is designed to be able to rotate through a large list of images, showing nine at a time. It also pre-loads six unseen images, the three next and the three previous images relative to the nine that are shown.

We thought of it in terms of a sliding window. For example, if we have twenty photos in our collection, we would store them in the master list like this (zero-based).
|0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|
|:|:|:|:|:|:|:|:|:|:|:-|:-|:-|:-|:-|:-|:-|:-|:-|:-|

Then there would be a sliding window indicating which nine images are currently showing:
|0|1|2|3|<b>[</b><font color='blue'>4</font>|<font color='blue'>5</font>|<font color='blue'>6</font>|<font color='blue'>7</font>|<font color='blue'>8</font>|<font color='blue'>9</font>|<font color='blue'>10</font>|<font color='blue'>11</font>|<font color='blue'>12</font><b>]</b>|13|14|15|16|17|18|19|
|:|:|:|:|:----------------------------------|:--------------------------|:--------------------------|:--------------------------|:--------------------------|:--------------------------|:---------------------------|:---------------------------|:-----------------------------------|:-|:-|:-|:-|:-|:-|:-|

There is second, wider window that also includes the pre-loaded images.
|0|<b>{</b><font color='gray'>1</font>|<font color='gray'>2</font>|<font color='gray'>3</font>|<b>[</b><font color='blue'>4</font>|<font color='blue'>5</font>|<font color='blue'>6</font>|<font color='blue'>7</font>|<font color='blue'>8</font>|<font color='blue'>9</font>|<font color='blue'>10</font>|<font color='blue'>11</font>|<font color='blue'>12</font><b>]</b>|<font color='gray'>13</font>|<font color='gray'>14</font>|<font color='gray'>15</font><b>}</b>|16|17|18|19|
|:|:----------------------------------|:--------------------------|:--------------------------|:----------------------------------|:--------------------------|:--------------------------|:--------------------------|:--------------------------|:--------------------------|:---------------------------|:---------------------------|:-----------------------------------|:---------------------------|:---------------------------|:-----------------------------------|:-|:-|:-|:-|

We use a single variable currentPhotoIndex to store this state. currentPhotoIndex is the zero-based index of the front-most photo. This is the photo at the center of our sliding window. We can then use this value and the constants for the number of images to show and the number to preload to figure out which images to load. For example, this window would represent the state when currentPhotoIndex is 8.
|0|<b>{</b><font color='gray'>1</font>|<font color='gray'>2</font>|<font color='gray'>3</font>|<b>[</b><font color='blue'>4</font>|<font color='blue'>5</font>|<font color='blue'>6</font>|<font color='blue'>7</font>|<font color='blue'><b><u>8</u></b></font>|<font color='blue'>9</font>|<font color='blue'>10</font>|<font color='blue'>11</font>|<font color='blue'>12</font><b>]</b>|<font color='gray'>13</font>|<font color='gray'>14</font>|<font color='gray'>15</font><b>}</b>|16|17|18|19|
|:|:----------------------------------|:--------------------------|:--------------------------|:----------------------------------|:--------------------------|:--------------------------|:--------------------------|:----------------------------------------|:--------------------------|:---------------------------|:---------------------------|:-----------------------------------|:---------------------------|:---------------------------|:-----------------------------------|:-|:-|:-|:-|

The master list only stores the URLs and captions of photos. The photos that are displayed or preloaded each have an Image widget that loads the image and displays if (if applicable). These images are stored in a separate array. Here are a couple of example states. Note how the window wraps.

currentPhotoIndex = 14
|<font color='gray'>0</font>|<font color='gray'>1</font><b>}</b>|2|3|4|5|6|<b>{</b><font color='gray'>7</font>|<font color='gray'>8</font>|<font color='gray'>9</font>|<b>[</b><font color='blue'>10</font>|<font color='blue'>11</font>|<font color='blue'>12</font>|<font color='blue'>13</font>|<font color='blue'><b><u>14</u></b></font>|<font color='blue'>15</font>|<font color='blue'>16</font>|<font color='blue'>17</font>|<font color='blue'>18</font><b>]</b>|<font color='gray'>19</font>|
|:--------------------------|:----------------------------------|:|:|:|:|:|:----------------------------------|:--------------------------|:--------------------------|:-----------------------------------|:---------------------------|:---------------------------|:---------------------------|:-----------------------------------------|:---------------------------|:---------------------------|:---------------------------|:-----------------------------------|:---------------------------|

|<b>{</b><font color='gray'>7</font>|<font color='gray'>8</font>|<font color='gray'>9</font>|<b>[</b><font color='blue'>10</font>|<font color='blue'>11</font>|<font color='blue'>12</font>|<font color='blue'>13</font>|<font color='blue'><b><u>14</u></b></font>|<font color='blue'>15</font>|<font color='blue'>16</font>|<font color='blue'>17</font>|<font color='blue'>18</font><b>]</b>|<font color='gray'>19</font>|<font color='gray'>0</font>|<font color='gray'>1</font><b>}</b>|
|:----------------------------------|:--------------------------|:--------------------------|:-----------------------------------|:---------------------------|:---------------------------|:---------------------------|:-----------------------------------------|:---------------------------|:---------------------------|:---------------------------|:-----------------------------------|:---------------------------|:--------------------------|:----------------------------------|


currentPhotoIndex = 0 (the initial state)
|<font color='blue'><b><u>0</u></b></font>|<font color='blue'>1</font>|<font color='blue'>2</font>|<font color='blue'>3</font>|<font color='blue'>4</font><b>]</b>|<font color='gray'>5</font>|<font color='gray'>6</font>|<font color='gray'>7</font><b>}</b>|8|9|10|11|12|<b>{</b><font color='gray'>13</font>|<font color='gray'>14</font>|<font color='gray'>15</font>|<b>[</b><font color='blue'>16</font>|<font color='blue'>17</font>|<font color='blue'>18</font>|<font color='blue'>19</font>|
|:----------------------------------------|:--------------------------|:--------------------------|:--------------------------|:----------------------------------|:--------------------------|:--------------------------|:----------------------------------|:|:|:-|:-|:-|:-----------------------------------|:---------------------------|:---------------------------|:-----------------------------------|:---------------------------|:---------------------------|:---------------------------|

|<b>{</b><font color='gray'>13</font>|<font color='gray'>14</font>|<font color='gray'>15</font>|<b>[</b><font color='blue'>16</font>|<font color='blue'>17</font>|<font color='blue'>18</font>|<font color='blue'>19</font>|<font color='blue'><b><u>0</u></b></font>|<font color='blue'>1</font>|<font color='blue'>2</font>|<font color='blue'>3</font>|<font color='blue'>4</font><b>]</b>|<font color='gray'>5</font>|<font color='gray'>6</font>|<font color='gray'>7</font><b>}</b>|
|:-----------------------------------|:---------------------------|:---------------------------|:-----------------------------------|:---------------------------|:---------------------------|:---------------------------|:----------------------------------------|:--------------------------|:--------------------------|:--------------------------|:----------------------------------|:--------------------------|:--------------------------|:----------------------------------|

The setCurrentPhotoIndex method of Carousel does the work of setting up the images in the smaller image array based on the currentPhotoIndex and the values in the master list. Now all we have to do is display the middle nine images of the image array in their correct spots on the screen (see Laying Out the Photos above) and hide the outer six.

Here is how the last example above (currentPhotoIndex = 0) would be rendered. (The images with gray boarders represent pre-loaded images that are not visible.)

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/numbered-images.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/numbered-images.gif)

### Smooth rotation ###
There is one final trick, though. We do not just switch photos between the nine fixed positions. Instead we need a smooth transition between positions. In fact we have a practically infinite number of states between the basic image positions.

We take care of this in a similar way, with a variable called currentRotation. While currentPhotoIndex is an integer type, currentRotation is a double. It reflects not only which photo is currently in front but also whatever distance it is from the exact front position.

currentRotation rounded to the nearest whole number is currentPhotoIndex. The remaining decimal is the fraction of the distance between base photo positions that all images are offset. Here are some examples.

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/rotation1.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/rotation1.gif)

**currentRotation: 1.0 --> currentPhotoIndex: 1 / extra rotation: 0.0 x 45° = 0.0°**

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/rotation2.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/rotation2.gif)

**currentRotation: 13.3 --> currentPhotoIndex: 13 / extra rotation: 0.3 x 45° = +13.5°**

![http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/rotation3.gif](http://spiral-carousel-gwt.googlecode.com/svn/wiki/images/rotation3.gif)

**currentRotation: 4.6 --> currentPhotoIndex: 5 / extra rotation: -0.6 x 45° = -27.0°**

What we do is expose this currentRotation as the interface to Carousel and use it to set currentPhotoIndex, which is only internally accessible.

### Making it Spin ###
In addition to exposing an API for setting the current rotation, the Carousel has APIs for making it spin continuously.

The setVelocity() method starts the carousel moving continuously. The value of velocity is in photos-per-millisecond so it should be quite small.

There is also a setAcceleration() method that tells the Carousel how quickly to slow down or speed up. Acceleration is actually a multiplier, so a value of 1.0 makes for a constant speed. The default value for acceleration is .998. This makes for a nice deceleration after the user drags the carousel to start it spinning.

An acceleration value less than 1.0 also makes another feature possible. The carousel can calculate how fast it needs to start spinning in order to come to rest at a certain point, given a certain deceleration rate. This feature is used by calling the rotateTo() and rotateBy() methods. rotateTo() sets the Carousel to a velocity that will cause it to come to rest at the specified rotation value (see currentRotation above). rotateBy() sets the Carousel to a velocity that will cause it to end up a certain distance from its current position. This distance is also measured in photos. Carousel also exposes next() and prev() methods that use rotateTo() to go to the next or previous photo.

### Interacting with the Carousel ###
What has been covered so far is the core behavior of the Carousel. Now what if we want to make the carousel interactive—for example allowing a person to spin through the photos or select a photo for a closer look?

We decided that user interaction should be defined separately from the core behavior of the Carousel. This allows the Carousel to be easily configured for use in a maximum variety of situations. To this end, there are two ways to enable user interaction with the Carousel widget.

The simple method is to add additional widgets to the page that call the Carousel API (i.e. setRotation(), setVelocity(), setAcceleration(), rotateBy(), rotateTo(), next(), prev()). A simple example would be adding "Next" and "Previous" buttons or "Start", "Stop" and "Reverse" buttons. Note that it might also be helpful to have access to the current state of the Carousel. The following methods are exposed for this purpose: getRotation(), getPhotoIndex(), getVelocity(), getAcceleration() and getCurrentPhoto().

The second method is to add event handlers to the Carousel widget to intercept mouse events and then call the Carousel API in response to those events. The Carousel exposes these standard mouse event handler registration methods...
  * addClickHandler()
  * addMouseDownHandler()
  * addMouseMoveHandler()
  * addMouseUpHandler()

The Carousel also exposes some of its own custom events...
  * addPhotoClickHandler() – Fired when any photo in the carousel is clicked.
  * addPhotoToFrontHandler() – Fired when a new photo is rotated into the front-most position.

### Or Just use the Default Behavior... ###
Now that's all fair and well, but who really wants to learn a new API and write a bunch of code for allowing their users to interact with the Carousel, when most of the users are going to be very happy with a standard way of doing things. Fear not! Carousel comes with default behavior that allows users to either spin the Carousel by dragging the mouse or spin to a specific photo just by clicking it.

To activate this default behavior... do nothing. Just use the default constructor and this behavior is automatically activated. To deactivate the default behavior either pass false to the "useDefaultMouseBehavior" constructor parameter or call setUseDefaultMouseBehavior(false) at any time.