package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
* Sample test scenario for web page scraping via joup based on chained node attribute scan
* that is more precise than browsing of immediate (grand-) children
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// Based on
//
// see also:
// https://www.baeldung.com/java-with-jsoup
// https://www.programcreek.com/java-api-examples/?class=org.jsoup.nodes.Document&method=getElementsByAttributeValue
// https://jsoup.org/cookbook/extracting-data/selector-syntax
// TODO: stop the chrome browser hanging in waiting for use.typekit.net
///
public class JsoupProbe2Test extends BaseTest {

	private static boolean debug = false;
	private static final Logger log = LogManager.getLogger(JsoupProbe2Test.class);
	private String filePath = "list2.html";
	// origin:
	// https://newenglandfarmlandfinder.org/property/1-acre-sale-several-greenhouses-residence-oneco-ct
	// for visual inspection
	// private String selector = "div.region-content fieldset.group-info
	// div.field-body";
	// verified
	private String selector = "div.region-content fieldset.group-property-tenure div.field-sale-price";
	private String pageSource = null;
	private static Document jsoupDocument;
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

	private static Document parentDocument;
	private static Elements jsoupElements;

	private static String attributeName;
	private static String attributeValue;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		pageSource = getScriptContent(filePath);
	}

	@Test(enabled = true)
	public void testVisual() {
		driver.navigate().to(getPageContent(filePath));
		pageSource = driver.getPageSource();
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
	/*
	 This can be used with Perl
	
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
		
		my $locatorChains = {
		    'info' => {
		        'names'  => [ 'class', 'class', 'class' ],
		        'values' => [
		            'region-content', ' group-info field-group-fieldset form-wrapper',
		            'field-body'
		        ]
		    },
		    'land_area' => {
		        'names'  => [ 'class', 'class', 'class' ],
		        'values' => [
		            'region-content',
		            ' group-property-land field-group-fieldset form-wrapper',
		            'field-acres-total inline'
		        ]
		    },
		
		    'price' => {
		        'names'  => [ 'class', 'class', 'class' ],
		        'values' => [
		            'region-content',
		            ' group-property-tenure field-group-fieldset form-wrapper',
		            'field-sale-price'
		        ]
		    }
		};
		my $results = {};
		
		foreach my $entry ( keys %$locatorChains ) {
		    $results->{$entry} = undef;
		    my $element = HTML::TagParser->new($filename);
		    my $names   = $locatorChains->{$entry}->{'names'};
		    my $values  = $locatorChains->{$entry}->{'values'};
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
		
	 */
}
