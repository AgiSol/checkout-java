package fi.agisol.checkout.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fi.agisol.checkout.api.dto.Bank;
import fi.agisol.checkout.api.dto.Trade;
import fi.agisol.checkout.exceptions.XmlParseException;

public class CheckoutXmlParser {

	private final Logger log = LoggerFactory.getLogger(CheckoutXmlParser.class);

	public Trade getTrade(InputStream xmlStream) {
		log.debug("Parsing Trade information from XML");
		Element root = getRootElement(xmlStream);
		Trade trade = null;
		if (root != null) {
			trade = parseTrade(root);
		}
		if (root == null || trade == null) {
			throw new XmlParseException(
					"Error parsing Checkout TradeResponse xml! See server logs for more information!");
		}
		
		return trade;
	}

	public CheckoutPaymentStatus getStatus(InputStream xmlStream) {

		CheckoutPaymentStatus status = CheckoutPaymentStatus.STATUS_TRANSACTION_UNFINISHED;
		Element root = getRootElement(xmlStream);

		if (root != null) {
			int statusValue = parseStatus(root);
			status = convertStatus(statusValue);
		} else {
			throw new XmlParseException(
					"Error parsing Checkout CheckResponse xml! See server logs for more information!");
		}

		return status;
	}

	private CheckoutPaymentStatus convertStatus(Integer statusValue) {
		CheckoutPaymentStatus status = CheckoutPaymentStatus.getByValue(statusValue);
		if (status == null) {
			throw new XmlParseException(
					"Error parsing Checkout CheckResponse xml! Unknown status:" + statusValue + ".");
		}
		return status;
	}

	private int parseStatus(Element tradeElement) {
		Integer resultStatus = null;
		try {
			resultStatus = Integer.valueOf(extractValueOfChild(tradeElement, "status"));
		} catch (Exception ex) {
			log.error("Error parsing status from CheckResponse xml", ex);
			throw new XmlParseException(
					"Error parsing status from CheckResponse xml! See server logs for more information!");
		}
		return resultStatus;
	}

	private Trade parseTrade(Element tradeElement) {

		Element paymentElement = getOneElementByName(tradeElement, "payment");
		Element banksElement = getOneElementByName(tradeElement, "banks");

		String description = extractValueOfChild(tradeElement, "description");
		String reference = extractValueOfChild(tradeElement, "reference");
		String firstName = extractValueOfChild(tradeElement, "firstname");
		String lastName = extractValueOfChild(tradeElement, "familyname");
		String stamp = extractValueOfChild(tradeElement, "stamp");
		String paymentId = extractValueOfChild(paymentElement, "id");
		Integer amount = Integer.valueOf(extractValueOfChild(paymentElement, "amount"));
		List<Bank> banks = parseBanks(banksElement);

		return new Trade(paymentId, stamp, amount, reference, description, firstName, lastName, banks);
	}

	private String extractValueOfChild(Element rootNode, String childName) {
		return extractValueOfTheElement(getNodeByNameFromChildren(rootNode, childName));
	}

	private Element getNodeByNameFromChildren(Node rootNode, String childName) {
		Element resultNode = null;
		for (Node child = rootNode.getFirstChild(); child != null; child = child.getNextSibling()) {

			if (child instanceof Element) {
				if (childName.equals(child.getNodeName())) {
					resultNode = (Element) child;
					break;
				}
			}
		}
		if (resultNode == null) {
			throw new XmlParseException(
					"Error parsing check out response xml!" + "Could not found child element named '" + childName
							+ "', from root node: " + rootNode.getNodeName());
		}
		return resultNode;
	}

	private String extractValueOfTheElement(Element element) {
		Node child = element.getFirstChild();
		return child != null ? child.getNodeValue() : null;
	}

	private Element getOneElementByName(Element root, String name) {
		NodeList elementList = root.getElementsByTagName(name);
		int count = elementList.getLength();

		if (count != 1) {
			throw new XmlParseException("Error parsing check out response xml!" + "Unknown amount of '" + count
					+ "' elements " + "(Expected: 1, Actual: " + count + ").");
		}
		return (Element) elementList.item(0);
	}

	private List<Bank> parseBanks(Element banks) {
		List<Bank> bankDtos = new ArrayList<Bank>();

		for (Node bank = banks.getFirstChild(); bank != null; bank = bank.getNextSibling()) {

			if (bank instanceof Element) {
				Bank bankDto = parseBank(bank);
				bankDtos.add(bankDto);
			}
		}
		return bankDtos;
	}

	private Bank parseBank(Node bank) {
		Element bankElement = (Element) bank;
		String url = bankElement.getAttribute("url");
		String icon = bankElement.getAttribute("icon");
		String name = bankElement.getAttribute("name");

		Map<String, String> properties = new HashMap<String, String>();

		for (Node prop = bank.getFirstChild(); prop != null; prop = prop.getNextSibling()) {
			if (prop instanceof Element) {
				if (prop.getFirstChild() != null) {
					String key = prop.getNodeName();
					String value = prop.getFirstChild().getNodeValue();
					if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
						properties.put(key, value);
					}
				}
			}
		}

		Bank bankDto = new Bank(name, url, icon, properties);
		return bankDto;
	}

	private Element getRootElement(InputStream body) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element root = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(body);
			root = document.getDocumentElement();

		} catch (Exception e) {
			log.error("Error parsing xml information from Checkout!", e);
			try {
				body.reset();
				Scanner scanner = new Scanner(body, "UTF-8");
				String bodyText = scanner.useDelimiter("\\A").next();
				scanner.close();
				log.error("Received invalid XML response from checkout: {}", bodyText);
			} catch (IOException e2) {
				log.error("Could not parse xml response stream..", e2);
			}
		}
		return root;
	}
}
