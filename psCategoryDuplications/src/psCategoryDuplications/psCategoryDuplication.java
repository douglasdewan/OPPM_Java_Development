package psCategoryDuplications;


import java.io.File;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 



import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
 
import org.w3c.dom.NodeList;

import java.io.StringWriter;

import org.w3c.dom.NamedNodeMap;

public class psCategoryDuplication 
{

	public static void main(String[] args)
	{
		if (args.length != 4)
		{
		      System.err.println ("Usage: java psCategoryDuplicatio <Input Package filename> <Output Package filename> \"Seacrh String\" \" Replace String\"");
		      System.exit (1);
		}
		
		try
		{
			File psPackage = new File(args[0]);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(psPackage);
			
			Element root = doc.getDocumentElement();  
			NodeList children = root.getChildNodes();
			
			NamedNodeMap rootElements = root.getAttributes();
			
			for(int rootindex=0;rootindex < rootElements.getLength();rootindex++)
			{
				System.out.println("Element Name: " + rootElements.item(rootindex).getNodeName());
				System.out.println("Element Value: " + rootElements.item(rootindex).getNodeValue());
			}
			
			System.out.println("Number of Nodes: "+ children.getLength());
			
			boolean MatchedString = false;
						
			printAllNodesAndInfo(children, args[2], args[3], MatchedString);
			
			// My attempt at listing things.  Will probably deprecate and delete.
			// int Depth = 1;
			// psGetAllChildNodes(children, Depth, args[2]);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			//transformer.transform(source, result);
			//String xmlString = result.getWriter().toString();
			//System.out.println(xmlString);
			
			StreamResult resultfile = new StreamResult(new File(args[1]));
			transformer.transform(source, resultfile);
			String xmlStringOut = result.getWriter().toString();
			System.out.println(xmlStringOut);
									
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
			
	}
	
	public static void psGetAllChildNodes(NodeList nodeList, int Depth, String OrigString)
	{
		
		
		for(int i=0; i<nodeList.getLength(); i++)
		{
		      Node childNode = nodeList.item(i);
		      
		      Depth++;
		      
		      System.out.println("Level - " + Depth + " - Node Name: " + childNode.getNodeName());
		      for (int elementindex = 0; elementindex < childNode.getAttributes().getLength(); elementindex++)
		      {
		    	  System.out.println("*****" + childNode.getAttributes() + ": " + childNode.getAttributes().item(elementindex).getNodeValue());
		      }
		      MatchString(nodeList,OrigString);

		      NodeList children = childNode.getChildNodes();
		      if (children != null)
		      {
		         psGetAllChildNodes(children,Depth,OrigString);
		         Depth--;
		      }
		}
	}
	
	public static void MatchString(NodeList nodeList, String MatchingString)
	{
				
		for (int i=0; i<nodeList.getLength();i++)
		{
						
			if (nodeList.item(i).getNodeValue() == MatchingString)
			{
				System.out.println("We have a match.  Node - " + nodeList.item(i).getNodeName());
			}
		}
	}
	
	private static void printAllNodesAndInfo(NodeList nodeList, String InputStringMatch, String OutputString, boolean MatchedString)
	{
		for (int count = 0; count < nodeList.getLength(); count++)
	    {
	 
		Node tempNode = nodeList.item(count);
				
		// make sure it's element node.
		if (tempNode.getNodeName() == "PS_ENUM_TYPE")
		{
			System.out.println("-------------  Skipping Value Lists -------------");
		}
		else if (tempNode.getNodeType() == Node.ELEMENT_NODE)
		{
	 
			// get node name and value
			System.out.println("\nNode Name = " + tempNode.getNodeName() + " [OPEN]");
			System.out.println("Node Value = " + tempNode.getTextContent());
			
			if (tempNode.hasAttributes() && ((tempNode.getNodeName() != "GUID"))) {
	 
				// get attributes names and values
				
				NamedNodeMap nodeMap = tempNode.getAttributes();
				
				for (int i = 0; i < nodeMap.getLength(); i++)
				{
	 				Node node = nodeMap.item(i);
					System.out.println("attr name : " + node.getNodeName());
					System.out.println("attr value : " + node.getNodeValue());
					
					// Check to see if the replacement string is in this value.
					// If it is print what the replacement value would look like.
					if (node.getNodeValue().contains(InputStringMatch))
					{
						System.out.println("\n\n" + "================  Match ================");
						System.out.println(node.getNodeValue().replaceAll(InputStringMatch,OutputString));
						System.out.println("================  End Match ================" + "\n\n");
						node.setNodeValue(node.getNodeValue().replaceAll(InputStringMatch,OutputString));
						MatchedString = true;
					}
					
				}
	 
			}
			else if((tempNode.getNodeName() == "GUID") && (tempNode.getNodeType() == Node.ELEMENT_NODE) && (MatchedString))
			{
				if (tempNode.hasAttributes())
				{
					 
					// get attributes names and values
					
					NamedNodeMap nodeMap = tempNode.getAttributes();
					
					for (int i = 0; i < nodeMap.getLength(); i++)
					{
		 				Node node = nodeMap.item(i);
						System.out.println("attr name : " + node.getNodeName());
						System.out.println("attr value : " + node.getNodeValue());
						
						// Check to see if the replacement string is in this value.
						// If it is print what the replacement value would look like.
						if (node.getNodeName() == "Value")
						{
							String NewGUID = String.valueOf(UUID.randomUUID());
							
							System.out.println("------------------------------------------------------");
							System.out.println("Old GUID: " + node.getNodeValue());
							System.out.println("New GUID: " + NewGUID);
							System.out.println("------------------------------------------------------");
													
							node.setNodeValue("{" + NewGUID + "}");
							System.out.println("New GUID in XML: " + node.getNodeValue());
							System.out.println("------------------------------------------------------");
						}
						
					}
				}
				MatchedString = false;
			}
	 
			if (tempNode.hasChildNodes())
			{
				// loop again if has child nodes
				printAllNodesAndInfo(tempNode.getChildNodes(), InputStringMatch, OutputString, MatchedString);
				MatchedString = false;
	 		}
	 
			System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
		}
	 
	    }
	 
	  }
}


