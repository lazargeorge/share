/**
 * Document List Component: Create New Node - create copy of node template in the Data Dictionary
 */
function main()
{
   // get the arguments - expecting the "sourceNodeRef" and "parentNodeRef" of the source node to copy
   // and the parent node to contain the new copy of the source.
   var sourceNodeRef = json.get("sourceNodeRef");
   if (sourceNodeRef == null || sourceNodeRef.textValue() == null || sourceNodeRef.textValue().length === 0)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "Mandatory 'sourceNodeRef' parameter missing.");
      return;
   }
   var parentNodeRef = json.get("parentNodeRef");
   if (parentNodeRef == null || parentNodeRef.textValue() == null || parentNodeRef.textValue().length === 0)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "Mandatory 'parentNodeRef' parameter missing.");
      return;
   }
   
   // get the nodes and perform the copy - permission failures etc. will produce a status code response
   var sourceNode = search.findNode(sourceNodeRef.textValue()),
       parentNode = search.findNode(parentNodeRef.textValue());
   if (sourceNode == null || parentNode == null)
   {
      status.setCode(status.STATUS_NOT_FOUND, "Source or destination node is missing for copy operation.");
   }
   model.result = sourceNode.copy(parentNode);
}

main();