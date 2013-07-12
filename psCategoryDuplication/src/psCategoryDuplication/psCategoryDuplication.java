package psCategoryDuplication;


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

		boolean[] bValidated = ValidateArgs(args);
		
		if (bValidated[3] == false)
		{
			System.err.print ("*****  Input File Not Found *****\n\n\n");
		}
		if (bValidated[4])
		{
			System.err.print ("*****  Too Many Arguments.  Verify that search strings and file names are in quotes (\") *****\n\n\n");
		}
		if (bValidated[5])
		{
			System.err.print ("*****  Too Few Arguments.  Verify that input file, output file, search and replace strings have all been supplied. *****\n\n\n");
		}
		if (bValidated[0])
		{
		      System.err.print ("Usage: java psCategoryDuplication [-v] <Input Package filename> <Output Package filename> \"Seacrh String\" \"Replace String\"");
		      System.err.print ("\n");
		      System.err.print ("\nSwitches:");
		      System.err.print ("\n		-v enables verbose mode");
		      System.err.print ("\n		-m Displays matching strings and replacement values.");
		      System.err.print ("\n		-x Displays the XML out file. Does not generate file.");
		      System.err.print ("\n		-s Display run parameters.");
		      System.exit (1);
		}
		
		try
		{
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			String sInputFileName = null;
			String sOutputFileName = null;
			String sInputSearchString = null;
			String sReplaceString = null;
			
			if(bValidated[7])
			{			
				if (bValidated[3])
				{
					sInputFileName = args[1];
				}
				sOutputFileName = args[2];
				sInputSearchString = args[3];
				sReplaceString = args[4];
			}
			else if((!bValidated[7]))
			{			
				if(bValidated[3])
				{
					sInputFileName = args[0];
				}
				sOutputFileName = args[1];
				sInputSearchString = args[2];
				sReplaceString = args[3];
			}
			
			if (bValidated[8])
			{
				System.out.println(	"Errors: " + 			bValidated[0] +
									"\nVerbose: " + 		bValidated[1] +
									"\nMatches: " + 		bValidated[2] +
									"\nFile Exists: " + 	bValidated[3] +
									"\nToo Many Args: " +	bValidated[4] +
									"\nToo Few Args: " + 	bValidated[5] +
									"\nXML Out: " + 		bValidated[6] +
									"\nSwitches Set: " + 	bValidated[7] + 
									"\nRun Parameters: " + 	bValidated[8]);
			}
			
			File psPackage = new File(sInputFileName);
			Document doc = dBuilder.parse(psPackage);
			
			Element root = doc.getDocumentElement();  
			NodeList children = root.getChildNodes();
			
			NamedNodeMap rootElements = root.getAttributes();
			
			if(bValidated[1])
			{
				for(int rootindex=0;rootindex < rootElements.getLength();rootindex++)
				{
					System.out.println("Element Name: " + rootElements.item(rootindex).getNodeName());
					System.out.println("Element Value: " + rootElements.item(rootindex).getNodeValue());
				}
			}
			
			// Needed an initial matching string to satisfy method passing.
			boolean MatchedString = false;
						
			PackageEvaluation(children, sInputSearchString, sReplaceString, MatchedString, bValidated[1], bValidated[2]);
			
			// Prepare for output to a file
									
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file or display to screen.
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			
			if((bValidated[1]) || bValidated[6])
			{
				transformer.transform(source, result);
				String xmlString = result.getWriter().toString();
				System.out.println(xmlString);
			}
			else if(!bValidated[6])
			{
				StreamResult resultfile = new StreamResult(new File(sOutputFileName));
				transformer.transform(source, resultfile);
				String xmlStringOut = result.getWriter().toString();
				System.out.println(xmlStringOut);
			}
									
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
			
	}
	
	public static boolean[] ValidateArgs(String[] args)
	{
		int index = 1;
		boolean bHasSwitches = false;
		boolean bVerbose = false;
		boolean bMatches = false;
		boolean bInputFileExist = false;
		boolean bErrors = true;
		boolean bTooManyArgs = true;
		boolean bTooFewArgs = true;
		boolean bDisplayXMLOnly = false;
		boolean bRunParameters = false;
		
		if(args[0].startsWith("-"))
		{
			bHasSwitches = true;
			while (index < args[0].length())
			{
				char cSwitch = args[0].charAt(index);
				switch (cSwitch)
				{
				case 'v':
					bVerbose = true;
					break;
				case 'm':
					bMatches = true;
					break;
				case 'x':
					bDisplayXMLOnly = true;
					break;
				case 's':
					bDisplayXMLOnly = true;
					break;
				}
				index++;
			}
			
			File fileInputPackage = new File(args[1]);
			if (fileInputPackage.exists())
			{
				bInputFileExist = true;
				bErrors = false;
			}
			else
			{
				bErrors = true;
			}
			if (args.length == 5) 
			{
				bTooFewArgs = false;
				bTooManyArgs = false;
			}
			else if (args.length < 5) 
			{
				bTooFewArgs = true;
				bTooManyArgs = false;
				bErrors = true;
			}
			else if (args.length > 5) 
			{
				bTooManyArgs = true;
				bTooFewArgs = false;
				bErrors = true;
			}
					
		}
		else if (!args[0].startsWith("-"))
		{
			File fileInputPackage = new File(args[0]);
			if (fileInputPackage.exists())
			{
				bInputFileExist = true;
				bErrors = false;
			}
			if (args.length == 4) 
			{
				bTooFewArgs = false;
				bTooManyArgs = false;
			}
			else if (args.length < 4) 
			{
				bTooFewArgs = true;
				bTooManyArgs = false;
				bErrors = true;
			}
			else if (args.length > 4) 
			{
				bTooManyArgs = true;
				bTooFewArgs = false;
				bErrors = true;
			}
		}
		
		boolean[] results = new boolean[9];
		results[0] = bErrors;
		results[1] = bVerbose;
		results[2] = bMatches;
		results[3] = bInputFileExist;
		results[4] = bTooManyArgs;
		results[5] = bTooFewArgs;
		results[6] = bDisplayXMLOnly;
		results[7] = bHasSwitches;
		results[8] = bRunParameters;
		return results;
	}
	
	private static void PackageEvaluation(NodeList nodeList, String InputStringMatch, String OutputString, boolean bMatchedString, boolean bVerbose, boolean bMatches)
	{
		for (int count = 0; count < nodeList.getLength(); count++)
	    {
		Node tempNode = nodeList.item(count);
		// We do not want to rewrite anything in Value Lists at this time. This skips the whole section.
		// Note this might cause some problems after package import as we do no validation of Value lists members.
		if (tempNode.getNodeName() == "PS_ENUM_TYPE")
		{
			if (bVerbose)
			{
				System.out.println("-------------  Skipping Value Lists -------------");
			}
		}
		else if (tempNode.getNodeType() == Node.ELEMENT_NODE)
		{
	 
			// get node name and value
			if (bVerbose)
			{
				System.out.println("\nNode Name = " + tempNode.getNodeName() + " [OPEN]");
				System.out.println("Node Value = " + tempNode.getTextContent());
			}
			
			if (tempNode.hasAttributes() && ((tempNode.getNodeName() != "GUID"))) {
	 
				// get attributes names and values
				
				NamedNodeMap nodeMap = tempNode.getAttributes();
				
				for (int i = 0; i < nodeMap.getLength(); i++)
				{
	 				Node node = nodeMap.item(i);
					if(bVerbose)
					{
						System.out.println("attr name : " + node.getNodeName());
						System.out.println("attr value : " + node.getNodeValue());
					}
					
					// Check to see if the replacement string is in this value.
					// If it is print what the replacement value would look like.
					if (node.getNodeValue().contains(InputStringMatch))
					{
						if((bVerbose) || (bMatches))
						{
							System.out.println("\n\n" + "================  Match ================");
							System.out.println(node.getNodeValue().replaceAll(InputStringMatch,OutputString));
							System.out.println("================  End Match ================" + "\n\n");
						}
						node.setNodeValue(node.getNodeValue().replaceAll(InputStringMatch,OutputString));
						bMatchedString = true;
					}
					
				}
	 		}
			else if((tempNode.getNodeName() == "GUID") && (tempNode.getNodeType() == Node.ELEMENT_NODE) && (bMatchedString))
			{
				// This section will replace the GUID value for a recently matched string section.
				// Need more testing to see if this handles all cases
				if (tempNode.hasAttributes())
				{
					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++)
					{
		 				Node node = nodeMap.item(i);
		 				if (bVerbose)
		 				{
		 					System.out.println("attr name : " + node.getNodeName());
		 					System.out.println("attr value : " + node.getNodeValue());
		 				}
						if (node.getNodeName() == "Value")
						{
							String NewGUID = String.valueOf(UUID.randomUUID());
							if((bVerbose)||(bMatches))
							{
								System.out.println("------------------------------------------------------");
								System.out.println("Old GUID: " + node.getNodeValue());
								System.out.println("New GUID: " + NewGUID);
								System.out.println("------------------------------------------------------");
							}
							node.setNodeValue("{" + NewGUID + "}");
						}
					}
				}
				bMatchedString = false;
			}
			if (tempNode.hasChildNodes())
			{
				// loop again if has child nodes
				PackageEvaluation(tempNode.getChildNodes(), InputStringMatch, OutputString, bMatchedString, bVerbose, bMatches);
				bMatchedString = false;
	 		}
			if(bVerbose)
			{
				System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
			}
		}
	    }
	  }
}


