Getting Started  {#gettingStarted}
=========================


I. Create new Maven project
---------------------------

1. **Define `pom.xml` file**
> An example is [here](../../docs/examples/pom.xml).<br>
> <i><b>Note</b>: `'InsertValueHere'` text should be replaced with company or project specific values.</i>
<br>

2. **Define Spring context**
> An example is [here](../../docs/examples/ApplicationContext.xml).<br>
> <i><b>Note</b>: `'InsertValueHere'` text should be replaced with company or project specific values.</i>
<br>

3. **Create property files in `resources` directory**
> Examples:<br>
> – [store.properties](../../docs/examples/store.properties)      (store main URL, HTTP and HTTPS ports)<br>
> – [pages.properties](../../docs/examples/pages.properties)      (store page URLs and titles)<br>
> – [jbehave.properties](../../docs/examples/browser.properties)  (browser name, number of threads, metafilters and mandatory jbehave settings)<br>
> – [browser.properties](../../docs/examples/browser.properties)  (proxy & loading timeout settings)<br>
> – [log4j.properties](../../docs/examples/log4j.properties)      (logging level & main log patterns)<br>
> – [mobile.properties](../../docs/examples/mobile.properties)    (mobile apps data)
<br>

4. **Develop tests using BDD approach**
> a) Write test stories <i>(in Given-When-Then style, find detailed syntax & examples [here](http://jbehave.org/reference/stable/story-syntax.html)).</i><br>
> b) Describe pages <i>(in new classes that extend [AbstractPage](@ref com.griddynamics.qa.ui.AbstractPage), then add these classes to Spring context, add URL & titles to <code>pages.properties</code> file).</i><br>
> c) Develop implementation of project-specific steps <i>(common steps should be re-used from [CommonPageSteps](@ref com.griddynamics.qa.ui.steps.CommonPageSteps) class).</i>
<br><br>


II. Run tests
-------------

### • Desktop browser

Common tests execution parameters are as follows:
<pre><code>-Dthreads=1 -Dsuite.all=**/*Suite.java -Dsuite.list=SmokeWebSuite clean test -P runTests</code></pre>

> Where<br>
> <b>–Dthreads</b> : Number of threads used to execute tests<br>
> <b>–Dsuite.all</b> : template for all suites to be included in execution <i>(empty value is not allowed; * wildcard can be used)</i><br>
> <b>–Dsuite.list</b> : specific suite(s) from template that should be executed <i>(can be empty or not specified – then all suites matching `suite.all` will be executed)</i><br>
> <b>–Dstory.list</b> : specific stories belonging the suites that should be executed <i>(can be empty or not specified – then all stories in suite(s) will be executed)</i><br>
<br>


### • Mobile browser & mobile apps

> <i><b>Note</b>: Please make sure you've installed [mobile prerequisites](@ref prerequisites-mobile) first.</i>

Common tests execution parameters are as follows:<br>
<pre><code>-Dmobile=android_app -Ddevice=true -Dsuite.all=**/*Suite.java -Dsuite.list=AndroidAppSuite clean test -P runTests</code></pre>

> Where<br>
> <b>–Dmobile</b> : mobile platform and type of application to test.<br>
> *Possible values: `android_app`, `android_browser`, `ios_app`, `ios_safari`.*<br>
> <b>–Ddevice</b> : if true real device is used, if false or not specified an emulator is used.<br>
> <b>–Dsuite.all</b>, <b>–Dsuite.list</b>, <b>–Dstory.list</b> : see above.
<br>