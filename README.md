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

