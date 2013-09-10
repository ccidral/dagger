Dagger
======

My own web framework, written to fullfil my own needs. I don't expect
it to be any famous, but if you find it useful please let me know. ` :) `


Why On Earth Did I Write a Web Framework?
----------------------------------------

Because I'm nuts. Well, not really (I guess). I did it to make it easy to write
client tests (e.g. Selenium tests) and run them as quickly as possible. It should
be possible to completely isolate client code from server code, and should also
provide a lightweight web server to be used on tests. Dagger makes it
possible to mock server routes (or controllers, or whatever) while not forcing us
to load the entire server application only to test a small portion of it.

No static methods to define routes.
No need to lift a whole Servlet container during tests.


Features
--------

* Everything is pluggable. Every tiny bit of Dagger is injectable.
  Small interfaces everywhere.
* The core doesn't depend upon the Servlet API.
* Websocket support (Experimental. Works fine for me.)
* Freemarker templates support. You can write your own template support if you want.
* Lightweight web server. Starts up quickly, can be injected with anything.
* Auto-reloadable development web server.
* Servlet support (coming soon).


But... There's Another Project Named "Dagger". What Now?
--------------------------------------------------------

Yes, [there's already another project called Dagger](https://github.com/square/dagger/).
I made my first commit a few days before the other Dagger project's first commit, but I
kept my project private at Bitbucket while it evolved, so technically I arrived later.
Anyway, as I said above I don't expect my project to get any famous so I don't mind
renaming it, which I will do as soon as I find a cool name for it.

License
-------

Dagger is licensed under the terms of the
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
