package psCategoryDuplications;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import org.w3c.dom.NamedNodeMap;

public class psCategoryDuplication 
{

	public static void main(String[] args)
	{
		try
		{
			File psPackage = new File(args[0]);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(psPackage);
			
			Element root = doc.getDocumentElement();  
			NodeList children = root.getChildNodes(); 
			
			System.out.println("Number of Nodes: "+ children.getLength());
			
			int Depth = 1;
			
			printNote(children, args[2]);
			
			// psGetAllChildNodes(children, Depth, args[2]);
			
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
	
	private static void printNote(NodeList nodeList, String InputStringMatch)
	{
		 
	    for (int count = 0; count < nodeList.getLength(); count++) {
	 
		Node tempNode = nodeList.item(count);
	 
		// make sure it's element node.
		if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	 
			// get node name and value
			System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
			System.out.println("Node Value =" + tempNode.getTextContent());
	 
			if (tempNode.hasAttributes()) {
	 
				// get attributes names and values
				NamedNodeMap nodeMap = tempNode.getAttributes();
	 
				for (int i = 0; i < nodeMap.getLength(); i++) {
	 
					Node node = nodeMap.item(i);
					System.out.println("attr name : " + node.getNodeName());
					System.out.println("attr value : " + node.getNodeValue());
					if (node.getNodeValue().contains(InputStringMatch))
					{
						System.out.println("================  Match ================");
					}
	 
				}
	 
			}
	 
			if (tempNode.hasChildNodes()) {
	 
				// loop again if has child nodes
				printNote(tempNode.getChildNodes(), InputStringMatch);
	 
			}
	 
			System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
	 
		}
	 
	    }
	 
	  }
}

