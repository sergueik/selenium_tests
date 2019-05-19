package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
* Sample test scenario for web page scraping with Jsoup and HTML::TagParser using on recordset of  node attribute scan
* which is more precise than browsing of immediate (grand-) children
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// See also:
// https://www.baeldung.com/java-with-jsoup
// https://www.programcreek.com/java-api-examples/?class=org.jsoup.nodes.Document&method=getElementsByAttributeValue
// https://jsoup.org/cookbook/extracting-data/selector-syntax
// test with firefox profile
// TODO: stop the chrome browser hanging in waiting for use.typekit.net
public class JsoupProbe2Test extends BaseTest {

	private static DumperOptions options = new DumperOptions();
	private static Yaml yaml = null;

	private static boolean debug = false;
	private static final Logger log = LogManager.getLogger(JsoupProbe2Test.class);
	private String htmlFilePath = "list2.html";

	// origin:
	// https://newenglandfarmlandfinder.org/property/1-acre-sale-several-greenhouses-residence-oneco-ct
	// for visual inspection
	// private String selector = "div.region-content fieldset.group-info
	// div.field-body";
	// verified
	private String selector = "div.region-content fieldset.group-property-tenure div.field-sale-price";
	private String pageSource = null;
	private static List<WebElement> elements;
	private final Map<String, Map<String, List<String>>> locatorChains = new HashMap<>();
	// TODO: (de)serialize through YAML
	private final List<String> attrKeys = Arrays
			.asList(new String[] { "class", "class", "class" });
	// NOTE: with a leading space in the DOM element attribute,
	// " group-info field-group-fieldset form-wrapper"
	// one would like to
	// do attribute filtering by exact value
	// but the HTML::TagParser Perl module has no equivalent
	// functionality
	/*
	private final List<String> attrValues = Arrays
			.asList(new String[] { "region-content", "group-info", "field-body" });
	
	private final List<String> attrValuesExact = Arrays
			.asList(new String[] { "region-content",
					" group-info field-group-fieldset form-wrapper", "field-body" });
	*/
	// verified

	private final List<String> attrValues = Arrays.asList(new String[] {
			"region-content", "group-property-tenure", "field-sale-price" });

	private final List<String> attrValuesExact = Arrays
			.asList(new String[] { "region-content",
					" group-property-tenure field-group-fieldset form-wrapper",
					"field-sale-price" });

	private static Document jsoupDocument;
	private static Document parentDocument;
	private static Elements jsoupElements;

	private static String attributeName;
	private static String attributeValue;
	private static String yamlFile = null;
	private static String internalConfiguration = String.format(
			"%s/src/test/resources/%s", System.getProperty("user.dir"),
			"existing.yaml");
	private static String writeFile = String.format("%s/src/test/resources/%s",
			System.getProperty("user.dir"), "generated.yaml");

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setBrowser("firefox");
		super.beforeClass();
		assertThat(driver, notNullValue());
		// don't know keys
		Map<String, Map<String, List<String>>> data = loadData(
				internalConfiguration);

		data.keySet().stream().limit(10).forEach(System.err::println);
		data.keySet().stream().limit(10).map(e -> data.get(e).get("names"))
				.forEach(System.err::println);
		data.keySet().stream().limit(10).map(e -> data.get(e).get("values"))
				.forEach(System.err::println);
		dumpData(data, writeFile);
		/*
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(writeFile),
					"UTF8");
			System.err.println("Dumping the config to: " + writeFile);
		
			yaml.dump(data, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	@SuppressWarnings("unchecked")
	public static void dumpData(Map<String, Map<String, List<String>>> data,
			String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			options.setExplicitStart(true);
			yaml = new Yaml(options);
		}
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(fileName),
					"UTF8");
			System.err.println("Dumping the config to: " + fileName);
			yaml.dump(data, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, List<String>>> loadData(
			String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			options.setExplicitStart(true);
			yaml = new Yaml(options);
		}

		Map<String, Map<String, List<String>>> data = new HashMap<>();
		try (InputStream in = Files.newInputStream(Paths.get(fileName))) {
			// NOTE: unchecked conversion
			// required: Map<String,Map<String,List<String>>>
			// found: capture#1 of ? extends java.util.Map
			data = yaml.loadAs(in, data.getClass());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	@BeforeMethod
	public void loadPage() {
		pageSource = getScriptContent(htmlFilePath);
	}

	@Test(enabled = true)
	public void testVisual() {
		driver.navigate().to(getPageContent(htmlFilePath));
		// pageSource = driver.getPageSource();
		elements = driver.findElements(By.cssSelector(selector));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		highlight(elements.get(0));
		System.err
				.println(String.format("Locating with Selenium: \"%s\"", selector));
		System.err.println("Data: " + elements.get(0).getText());
	}

	@Test(enabled = true)
	public void testAttributeValueContaining() {
		jsoupDocument = Jsoup.parse(pageSource);

		parentDocument = jsoupDocument;
		int lastStep = attrKeys.size() - 1;
		for (int step = 0; step <= lastStep; step++) {
			attributeName = attrKeys.get(step);
			attributeValue = attrValues.get(step);
			System.err.println(
					"Processing \"" + attributeName + "\" *= \"" + attributeValue + "\"");
			jsoupElements = parentDocument
					.getElementsByAttributeValueContaining(attributeName, attributeValue);
			if (debug) {
				if (jsoupElements != null && jsoupElements.size() > 0) {
					System.err.println("Found " + attributeName + "=" + attributeValue);
				}
			}
			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(0));

			if (lastStep == step) {
				System.err.println("Data: " + jsoupElements.first().text());
			} else {
				String innerHTML = jsoupElements.first().outerHtml();
				if (debug) {
					System.err.println(
							String.format("Processing attribute(\"%s\") = \"%s\" %s...",
									attributeName, attributeValue, innerHTML.substring(0, 160)));
				}
				System.err.println(String.format("Full attribute: \"%s\" = \"%s\"",
						attributeName, jsoupElements.first().attr(attributeName)));

				parentDocument = Jsoup.parse(innerHTML);
			}
		}
	}

	@Test(enabled = true)
	public void testAttributeValueExact() {
		jsoupDocument = Jsoup.parse(pageSource);

		parentDocument = jsoupDocument;
		int lastStep = attrKeys.size() - 1;
		for (int step = 0; step <= lastStep; step++) {
			attributeName = attrKeys.get(step);
			attributeValue = attrValuesExact.get(step);
			System.err.println(
					"Processing \"" + attributeName + "\" = \"" + attributeValue + "\"");
			jsoupElements = parentDocument.getElementsByAttributeValue(attributeName,
					attributeValue);
			if (debug) {
				if (jsoupElements != null && jsoupElements.size() > 0) {
					System.err.println("Found " + attributeName + "=" + attributeValue);
				}
			}
			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(0));

			if (lastStep == step) {
				System.err.println("Data: " + jsoupElements.first().text());
			} else {
				String innerHTML = jsoupElements.first().outerHtml();
				if (debug) {
					System.err.println(
							String.format("Processing attribute(\"%s\") = \"%s\" %s...",
									attributeName, attributeValue, innerHTML.substring(0, 160)));
				}
				parentDocument = Jsoup.parse(innerHTML);
			}
		}
	}
	// This can be used with Perl

	/*
	
		#!/usr/bin/perl
	
		use warnings;
		use strict;
		
		use Getopt::Long;
		use Data::Dumper qw(Dumper);
		use List::Util qw(max);
		use HTML::TagParser;
		
		use vars qw($DEBUG $MAX $DATA);
		$DEBUG = 0;
		
		sub getSubTree($$$) {
		    my ( $e, $n, $v ) = @_;
		    my @e = $e->getElementsByAttribute( $n, $v );
		    $e[0]->subTree();
		}
		
		sub getData($$$) {
		    my ( $e, $n, $v ) = @_;
		    my @e = $e->getElementsByAttribute( $n, $v );
		    my @d =
		      map { my $t = $_->innerText; $t =~ s|\s+| |g; $t } @e;
		    \@d;
		}
		my $filename = 'list2.html';
    << EOF
---
description:
  names:
    - 'class'
    - 'class'
    - 'class'
  values:
    - 'region-content'
    - ' group-info field-group-fieldset form-wrapper'
    - 'field-body'
price:
  names:
    - 'class'
    - 'class'
    - 'class'
  values:
    - 'region-content'
    - ' group-property-tenure field-group-fieldset form-wrapper'
    - 'field-sale-price'
land_area:
  names:
    - 'class'
    - 'class'
    - 'class'
  values:
    - 'region-content'
    - ' group-property-land field-group-fieldset form-wrapper'
    - 'field-acres-total inline'
EOF
		my $results = {};
		my $config = LoadFile('existing.yaml');
		foreach my $entry ( keys %$config ) {
		    $results->{$entry} = undef;
		    my $element = HTML::TagParser->new($filename);
		    my $names   = $config->{$entry}->{'names'};
		    my $values  = $config->{$entry}->{'values'};
		    if ($DEBUG) {
		        print Dumper($names);
		        print Dumper($values);
		    }
		    foreach my $step ( 0 ... $#$names ) {
		        if ( $step == $#$names ) {
		            my $data = getData( $element, $names->[$step], $values->[$step] );
		            print Dumper \$data if $DEBUG;
		            $results->{$entry} = $data->[0];
		        }
		        else {
		            $element =
		              getSubTree( $element, $names->[$step], $values->[$step] );
		        }
		        print $step if $DEBUG;
		    }
		}
		
		print Dumper \$results;
		$VAR1 = \{
            'land_area' => '1.0',
            'price' => '$190,000',
            'description' => 'Small fields...'
          };

	 */
}
