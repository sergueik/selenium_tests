### Info

The project practices Java with selected Selenium test scenarios using the test practice sites:

  * [http://suvian.in/selenium](http://suvian.in/selenium) - no longer active.
  * [http://www.way2automation.com](http://www.way2automation.com)

  and misc. standalone examples found on

  * [https://www.skyscanner.com](https://www.skyscanner.com)
  * [https://embed.plnkr.co/](https://embed.plnkr.co/)
  * [https://datatables.net/examples/api/form.html](https://datatables.net/examples/api/form.html) and [https://datatables.net/extensions/rowgroup/examples/initialisation/customRow.html](https://datatables.net/extensions/rowgroup/examples/initialisation/customRow.html)(https://datatables.net/examples/api/form.html)
  * [https://select2.github.io/examples.html](https://select2.github.io/examples.html)
  * [http://phppot.com/demo/jquery-dependent-dropdown-list-countries-and-states/]()
  * [http://jqueryui.com/datepicker/#buttonbar](http://jqueryui.com/datepicker/#buttonbar)
  * [http://demos.telerik.com/kendo-ui/grid/index](http://demos.telerik.com/kendo-ui/grid/index)
  * [http://antenna.io/demo/jquery-bar-rating/examples/](http://antenna.io/demo/jquery-bar-rating/examples/)
  * [http://www.seleniumeasy.com/test](http://www.seleniumeasy.com/test)
  * [https://v4-alpha.getbootstrap.com/components/forms/](https://v4-alpha.getbootstrap.com/components/forms/)
 *  [fjasonrobot/FancyWaiting](http://stackoverflow.com/questions/30174546/selenium-filter-with-predicate )


### Profiles

Project uses profiles to supports several browsers, possible to select through profile. The detauls browser is `chrome`.
```cmd
mvm -P[chrome|edge|firefox] test
```
Project exercises writing the property file into the `target` directory with converting the environment variable `TEST_PASSWORD` into
the property file entry:
```sh
export TEST_PASSWPORD='super secret'
mvn -Pproperties test
grep password target/classes/test.properties
password=super secret
```
Note:  when not set, it will not become blank, but rather the literal expression used in the `pom.xml`
```sh
unset TEST_PASSWORD
mvn -Pproperties clean test
# if password is crucial for the test, the test will fail
grep password target/classes/test.properties
password=${env.TEST_PASSWORD}
```
### Printing

```
INFO: Using `new ChromeOptions()` is preferred to `DesiredCapabilities.chrome()`

Starting ChromeDriver 2.37.544315 (730aa6a5fdba159ac9f4c1e8cbc59bf1b5ce12b7) onport 20505
Only local connections are allowed.
Sep 24, 2026 12:46:22 PM org.openqa.selenium.remote.ProtocolHandshake createSession
INFO: Detected dialect: OSS
Test Name: test2

Testing local file: file:/C:/developer/sergueik/selenium_tests/target/test-classes/svg_test.html
Script Console Log: SUCCESS
START
selector=svg#diagram
filename=selenium_test.txt
element found
tag=svg
id=diagram
width=600
height=150
serializing
serialized, length=88
blob created, size=88
object URL created
assigning img.src
img.src assigned
IMAGE ONLOAD
naturalWidth=600
naturalHeight=150
canvas created
canvas context created
drawImage completed
toDataURL completed
PNG length=2678
Click received, propagating and navigating normally!
download click completed
Continue waiting
Continue waiting
Continue waiting
Continue waiting
Timed out waiting for file
Test Name: test3

Testing local file: file:/C:/developer/sergueik/selenium_tests/target/test-classes/svg_test.html
Script Console Log: SUCCESS
START
selector=svg#diagram
filename=svg.png
element found
tag=svg
id=diagram
width=600
height=150
serializing
serialized, length=88
blob created, size=88
object URL created
assigning img.src
img.src assigned
IMAGE ONLOAD
naturalWidth=600
naturalHeight=150
canvas created
canvas context created
drawImage completed
toDataURL completed
PNG length=2678
download link created
download filename=svg.png
download click completed
Continue waiting
Continue waiting
Continue waiting
Continue waiting
Timed out waiting for file
Test Name: test4

Testing local file: file:/C:/developer/sergueik/selenium_tests/target/test-classes/mermaid_test.html
Script Console Log: FAIL
START
selector=svg#graph1
filename=graph.png
element found
tag=svg
id=graph1
width=743.7999877929688
height=349.7124938964844
serializing
serialized, length=18541
blob created, size=18541
object URL created
assigning img.src
img.src assigned
IMAGE ONLOAD
naturalWidth=300
naturalHeight=141
canvas created
canvas context created
drawImage completed
ONLOAD ERROR
SecurityError
Failed to execute 'toDataURL' on 'HTMLCanvasElement': Tainted canvases may not be exported.
Continue waiting
Continue waiting
Continue waiting
Continue waiting
Timed out waiting for file
Test Name: test5

Testing local file: file:/C:/developer/sergueik/selenium_tests/target/test-classes/mermaid_test.html
Script Console Log: FAIL
START
selector=svg#graph1
filename=graph.png
element found
tag=svg
id=graph1
width=743.7999877929688
height=349.7124938964844
serializing
serialized, length=18541
blob created, size=18541
object URL created
assigning img.src
img.src assigned
IMAGE ONLOAD
naturalWidth=300
naturalHeight=141
canvas created
canvas context created
drawImage completed
ONLOAD ERROR
SecurityError
Failed to execute 'toDataURL' on 'HTMLCanvasElement': Tainted canvases may not be exported.
Continue waiting
Continue waiting
Continue waiting
Continue waiting
Timed out waiting for file
Killing the process: chromedriver.exe
Matching item: "scoped_dir1780_29565"
Matching item: "scoped_dir1780_7247"
About to remove: C:\Users\sergueik\AppData\Local\Temp\scoped_dir1780_29565
About to remove: C:\Users\sergueik\AppData\Local\Temp\scoped_dir1780_7247
Removing: C:\Users\sergueik\AppData\Local\Temp\scoped_dir1780_29565
Removing: C:\Users\sergueik\AppData\Local\Temp\scoped_dir1780_7247
[ERROR] Tests run: 4, Failures: 3, Errors: 0, Skipped: 0, Time elapsed: 131.983s <<< FAILURE! - in com.github.sergueik.selenium.BrowserPrintSvgTest
[ERROR] test2(com.github.sergueik.selenium.BrowserPrintSvgTest)  Time elapsed: 30.602 s  <<< FAILURE!
com.github.sergueik.selenium.BrowserPrintSvgTest$DownloadTimeoutException: Timed out waiting for file C:\Users\sergueik\Downloads\selenium_test.txt over 00:00:30
        at com.github.sergueik.selenium.BrowserPrintSvgTest.waitDownloadFileExists(BrowserPrintSvgTest.java:256)
        at com.github.sergueik.selenium.BrowserPrintSvgTest.waitDownloadFileExists(BrowserPrintSvgTest.java:238)
        at com.github.sergueik.selenium.BrowserPrintSvgTest.test2(BrowserPrintSvgTest.java:133)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.testng.internal.invokers.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:136)
        at org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:658)
        at org.testng.internal.invokers.TestInvoker.invokeTestMethod(TestInvoker.java:219)
        at org.testng.internal.invokers.MethodRunner.runInSequence(MethodRunner.java:50)
        at org.testng.internal.invokers.TestInvoker$MethodInvocationAgent.invoke(TestInvoker.java:923)
        at org.testng.internal.invokers.TestInvoker.invokeTestMethods(TestInvoker.java:192)
        at org.testng.internal.invokers.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:146)
        at org.testng.internal.invokers.TestMethodWorker.run(TestMethodWorker.java:128)
        at java.util.ArrayList.forEach(ArrayList.java:1249)
        at org.testng.TestRunner.privateRun(TestRunner.java:808)
        at org.testng.TestRunner.run(TestRunner.java:603)
        at org.testng.SuiteRunner.runTest(SuiteRunner.java:429)
        at org.testng.SuiteRunner.runSequentially(SuiteRunner.java:423)
        at org.testng.SuiteRunner.privateRun(SuiteRunner.java:383)
        at org.testng.SuiteRunner.run(SuiteRunner.java:326)
        at org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)
        at org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:95)
        at org.testng.TestNG.runSuitesSequentially(TestNG.java:1249)
        at org.testng.TestNG.runSuitesLocally(TestNG.java:1169)
        at org.testng.TestNG.runSuites(TestNG.java:1092)
        at org.testng.TestNG.run(TestNG.java:1060)
        at org.apache.maven.surefire.testng.TestNGExecutor.run(TestNGExecutor.java:135)
        at org.apache.maven.surefire.testng.TestNGDirectoryTestSuite.executeSingleClass(TestNGDirectoryTestSuite.java:112)
        at org.apache.maven.surefire.testng.TestNGDirectoryTestSuite.execute(TestNGDirectoryTestSuite.java:99)
        at org.apache.maven.surefire.testng.TestNGProvider.invoke(TestNGProvider.java:146)
        at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:386)
        at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:323)
        at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:143)

[ERROR] test3(com.github.sergueik.selenium.BrowserPrintSvgTest)  Time elapsed: 30.171 s  <<< FAILURE!
com.github.sergueik.selenium.BrowserPrintSvgTest$DownloadTimeoutException: Timed out waiting for file C:\Users\sergueik\Downloads\svg.png over 00:00:30
        at com.github.sergueik.selenium.BrowserPrintSvgTest.waitDownloadFileExists(BrowserPrintSvgTest.java:256)
        at com.github.sergueik.selenium.BrowserPrintSvgTest.waitDownloadFileExists(BrowserPrintSvgTest.java:238)
        at com.github.sergueik.selenium.BrowserPrintSvgTest.test3(BrowserPrintSvgTest.java:166)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.testng.internal.invokers.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:136)
        at org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:658)
        at org.testng.internal.invokers.TestInvoker.invokeTestMethod(TestInvoker.java:219)
        at org.testng.internal.invokers.MethodRunner.runInSequence(MethodRunner.java:50)
        at org.testng.internal.invokers.TestInvoker$MethodInvocationAgent.invoke(TestInvoker.java:923)
        at org.testng.internal.invokers.TestInvoker.invokeTestMethods(TestInvoker.java:192)
        at org.testng.internal.invokers.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:146)
        at org.testng.internal.invokers.TestMethodWorker.run(TestMethodWorker.java:128)
        at java.util.ArrayList.forEach(ArrayList.java:1249)
        at org.testng.TestRunner.privateRun(TestRunner.java:808)
        at org.testng.TestRunner.run(TestRunner.java:603)
        at org.testng.SuiteRunner.runTest(SuiteRunner.java:429)
        at org.testng.SuiteRunner.runSequentially(SuiteRunner.java:423)
        at org.testng.SuiteRunner.privateRun(SuiteRunner.java:383)
        at org.testng.SuiteRunner.run(SuiteRunner.java:326)
        at org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)
        at org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:95)
        at org.testng.TestNG.runSuitesSequentially(TestNG.java:1249)
        at org.testng.TestNG.runSuitesLocally(TestNG.java:1169)
        at org.testng.TestNG.runSuites(TestNG.java:1092)
        at org.testng.TestNG.run(TestNG.java:1060)
        at org.apache.maven.surefire.testng.TestNGExecutor.run(TestNGExecutor.java:135)
        at org.apache.maven.surefire.testng.TestNGDirectoryTestSuite.executeSingleClass(TestNGDirectoryTestSuite.java:112)
        at org.apache.maven.surefire.testng.TestNGDirectoryTestSuite.execute(TestNGDirectoryTestSuite.java:99)
        at org.apache.maven.surefire.testng.TestNGProvider.invoke(TestNGProvider.java:146)
        at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:386)
        at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:323)
        at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:
143)

[ERROR] test5(com.github.sergueik.selenium.BrowserPrintSvgTest)  Time elapsed: 30.828 s  <<< FAILURE!
java.lang.AssertionError:

Expected: is <true>
     but: was <false>
        at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:18)
        at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:6)
        at com.github.sergueik.selenium.BrowserPrintSvgTest.test5(BrowserPrintSvgTest.java:226)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.testng.internal.invokers.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:136)
        at org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:658)
        at org.testng.internal.invokers.TestInvoker.invokeTestMethod(TestInvoker.java:219)
        at org.testng.internal.invokers.MethodRunner.runInSequence(MethodRunner.java:50)
        at org.testng.internal.invokers.TestInvoker$MethodInvocationAgent.invoke(TestInvoker.java:923)
        at org.testng.internal.invokers.TestInvoker.invokeTestMethods(TestInvoker.java:192)
        at org.testng.internal.invokers.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:146)
        at org.testng.internal.invokers.TestMethodWorker.run(TestMethodWorker.java:128)
        at java.util.ArrayList.forEach(ArrayList.java:1249)
        at org.testng.TestRunner.privateRun(TestRunner.java:808)
        at org.testng.TestRunner.run(TestRunner.java:603)
        at org.testng.SuiteRunner.runTest(SuiteRunner.java:429)
        at org.testng.SuiteRunner.runSequentially(SuiteRunner.java:423)
        at org.testng.SuiteRunner.privateRun(SuiteRunner.java:383)
        at org.testng.SuiteRunner.run(SuiteRunner.java:326)
        at org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)
        at org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:95)
        at org.testng.TestNG.runSuitesSequentially(TestNG.java:1249)
        at org.testng.TestNG.runSuitesLocally(TestNG.java:1169)
        at org.testng.TestNG.runSuites(TestNG.java:1092)
        at org.testng.TestNG.run(TestNG.java:1060)
        at org.apache.maven.surefire.testng.TestNGExecutor.run(TestNGExecutor.java:135)
        at org.apache.maven.surefire.testng.TestNGDirectoryTestSuite.executeSingleClass(TestNGDirectoryTestSuite.java:112)
        at org.apache.maven.surefire.testng.TestNGDirectoryTestSuite.execute(TestNGDirectoryTestSuite.java:99)
        at org.apache.maven.surefire.testng.TestNGProvider.invoke(TestNGProvider.java:146)
        at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:386)
        at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:323)
        at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:143)

[INFO]
[INFO] Results:
[INFO]
[ERROR] Failures:
[ERROR]   BrowserPrintSvgTest.test2:133->waitDownloadFileExists:238->waitDownloadFileExists:256 DownloadTimeout
[ERROR]   BrowserPrintSvgTest.test3:166->waitDownloadFileExists:238->waitDownloadFileExists:256 DownloadTimeout
[ERROR]   BrowserPrintSvgTest.test5:226
Expected: is <true>
     but: was <false>
[INFO]
[ERROR] Tests run: 4, Failures: 3, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 02:15 min
[INFO] Finished at: 2026-09-24T12:48:30-07:00
[INFO] Final Memory: 18M/247M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.20:test (unit-tests) on project selenium_tests: There are test failures.
[ERROR]
[ERROR] Please refer to C:\developer\sergueik\selenium_tests\target\surefire-reports for the individual test results.
[ERROR] Please refer to dump files (if any exist) [date]-jvmRun[N].dump, [date].
dumpstream and [date]-jvmRun[N].dumpstream.
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException


```
### Links

 * [stackoverflow](http://stackoverflow.com/questions/30174546/selenium-filter-with-predicate)
 * [seleniumcapsules](https://github.com/yujunliang/seleniumcapsules)
 * [ahussan/Java8LamdaExpressionAndStreamAPITest](https://github.com/ahussan/Java8LamdaExpressionAndStreamAPITest)
 * [sskorol/selenium-camp-17](https://github.com/sskorol/selenium-camp-17)
 * [cssSelector advantages review](https://www.pineboat.in/post/css-selectors-selenium-webdriver-find-element-xpath-replaced/)
 * [JQuery to vanilla JavaScript DSL conversion rules](https://gist.github.com/dorokhin/8d47e91676a39c04534ea9633b98dbdb)
 * Elemental Selenium Tips [repo](https://github.com/tourdedave/elemental-selenium-tips) 
 * __at.info__ [test automation examples on different tools and technologies](http://atinfo.github.io/at.info-knowledge-base/)  (in Russian and Eglish )
 * KB of [xpath, locators, css-selectors](https://automated-testing.info/t/sbornik-poleznyh-ssylok-po-razlichnym-lokatoram/5316) (in Russian)
 * https://abstracta.us/blog/software-testing/best-demo-websites-for-practicing-different-types-of-software-tests/
 * https://www.techlistic.com/2020/07/automation-testing-demo-websites.html
 * [list of existing headless web browsers](https://github.com/dhamaniasad/HeadlessBrowsers)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)

