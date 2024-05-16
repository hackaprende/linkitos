# Linkitos
A small application that allows you to shorten links and displays a
history of the recently shortened links to your favorite websites.

# Instructions
Add a link to the text field and tap the arrow button to shorten it
Links sort from most to least recent
Click on a shortened link to open it
Long click a link to copy it to the Clipboard

# Architecture and structure
I used MVVM because it fits really well with Android and its components like Jetpack Compose, also
provides a great structure for testing and maintenance.

I divided the files in View - ViewModel - Use Case - Repository - ApiService, each file is decoupled
from the others following the Separation of Concerns principle, this makes possible to test each file 
separately and have a more robust app.

The app is separated in packages using features, not layers, this in my opinion is better because 
it's much easier to search for a class in particular. So for example instead of having packages:
viewmodels, repositories, views, etc. I sort the packages by screen, in this case we just have 
linklist screen, but if we had others we could have packages: login, linkdetails, profile, etc.
Also, this app is so simple it only has one module, but the recommendation is having several modules
when it grows. Some general classes like api files, dependency injection or models that are going 
to be used in the whole app have their own packages, if the app grows, a "Core" module is 
recommended so all other modules make use of it.

Naming convention is featureClassType, for example if we have a screen with a list of links the 
name of the file is LinkListScreen, same for LinkListViewModel, other examples in case the app grows
could be LinkDetailsViewModel, ProfileScreen, ProfileViewModel, etc. For the Repositories I
use the model with which they are working, LinkRepository, UserRepository, etc.

I used Use cases although documentation states they are optional:
"You should only use it when needed, for example, to handle complexity or favor reusability."
but I took the opportunity assuming this could be a more complex project in the future, in this case
it did not represent a bigger effort. If the effort was bigger I would not have included use cases 
for now. 
More info here[https://developer.android.com/topic/architecture/domain-layer#use-cases-kotlin].

# Libraries used
I used some of the most standard and robust libraries for Android
- Retrofit for http requests.
- Moshi for Json parsing.
- Hilt for Dependency Injection.

I opted for using flows instead of just coroutines because of the really good 
integration with States, MVVM and Jetpack Compose, it just feels natural.

# Testing

All the files are tested with Unit Tests, with exception of LinkListScreen, which is a View so it's
tested with UI integration Test using ComposeTestRule.

I opted for using ComposeTestRule instead of Espresso or AndroidComposeTestRule since this is a 
simple app with just one screen, this, in addition with that we are using Jetpack Compose,
makes it a lot easier, simpler and faster than using Espresso or AndroidComposeTestRule. Tests run
faster too.

All the tests take advantage of Dependency Injection, so we are using fakes instead of using 
mocks with a library like Mockito, this makes the tests more reliable and less prone to be flaky
since we have total control over what we are testing and the conditions we need to have for each 
test.

You can run all the Unit tests with this command:
./gradlew test

And for the UI tests (You need to have a device or emulator for these ones):
./gradlew connectedAndroidTest

You can also use "one ring to run them all and in the darkness bind them" 👁️:
./gradlew test connectedAndroidTest

Also you can check for code smell with lint
./gradlew lint

