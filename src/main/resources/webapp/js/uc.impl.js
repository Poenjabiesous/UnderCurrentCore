
 function authAndLogin(){

        var baseServerUrl="http://"+location.hostname;
        var coreAuthUrl=baseServerUrl+":777/undercurrentcore/auth";

        $.ajax({
                url: coreAuthUrl + "?secretKey="+ document.getElementById("secretKey").value,
                type: "GET",
                dataType: "json",
                success: function(responseData) {
                    if(responseData.data.auth == true)
                    {
                      var url = "viewblocks.html?secretKey=" + encodeURIComponent(document.getElementById("secretKey").value);
                      window.location.href = url;
                    }
                    else
                    {
                        document.getElementById("info").innerText = "Could not log you in. Secret key not registered on this server.";
                    }
                },
                 error: function(xhr, status, error) {
                     document.getElementById("info").innerText = "Error communicating with server: "+ status + ":" + xhr.responseText;
                }
            });
     }



    function populateBlocks()
    {

     var baseServerUrl="http://"+location.hostname;
     var coreCoreUrl=baseServerUrl+":777/undercurrentcore/core";

     $.ajax({
             url: coreCoreUrl+"?secretKey="+ getUrlParameter("secretKey"),
             type: "GET",
                   dataType: "json",
                   success: function(responseData) {

                    if(responseData.status == true)
                    {
                        if(countBlocks(responseData) == 0)
                        {
                            document.getElementById("info").innerText = "You do not own any UnderCurrent blocks yet.";
                            return;
                        }

                        for(var i = 0; i < countBlocks(responseData); i++)
                        {
                            var block = getBlockByIndex(responseData, i);
                            $("#blocklist").append("<li><a id='"+block.internalName+"' title='X:" + block.xCoord + " Y:" + block.yCoord+ " Z:" + block.zCoord +"' class='button big special' onclick='editBlock(this)'>"+block.name+"</a></li>");
                        }
                    }
                    else
                    {
                        document.getElementById("info").innerText = "Server error::"+resolveErrorType(responseData.error_message);
                    }
                    },
                    error: function(xhr, status, error) {
                        document.getElementById("info").innerText = "Error communicating with server: " + status + "::" + xhr.responseText;
                    }
                });
     }

      function populateEditableFields()
         {

          var baseServerUrl="http://"+location.hostname;
          var coreCoreUrl=baseServerUrl+":777/undercurrentcore/core";
          var blockInternalName = getUrlParameter("internalName");
          var secretKey = getUrlParameter("secretKey");
          var internalName = getUrlParameter("internalName");

          $.ajax({
                  url: coreCoreUrl+"?secretKey="+ secretKey ,
                  type: "GET",
                        dataType: "json",
                        success: function(responseData) {

                         if(responseData.status == true)
                         {
                             if(countBlocks(responseData) == 0)
                             {
                                 document.getElementById("info").innerText = "You do not own any UnderCurrent blocks yet.";
                                 return;
                             }

                             for(var i = 0; i < countBlocks(responseData); i++)
                             {
                                 var block = getBlockByIndex(responseData, i);
                                 if(block.internalName == internalName)
                                 {
                                     var collections = getBlockCollections(responseData, i);

                                     for(var j = 0; j < collections.length; j++)
                                     {
                                     var collection = collections[j];
                                     var editableFields = collection.editableFields;

                                     $("#fieldList").append("<h3>"+block.name+"</h3>");

                                        for(var k = 0; k < editableFields.length; k++)
                                        {
                                            var editableField = editableFields[k];
                                            if(editableField.editorType == "INFO")
                                            {
                                                $("#fieldList").append("<ul id='ul"+k+"'>"
                                                + "<li><label>"+editableField.displayName+"</label></li>"
                                                + "<li><p>"+editableField.fieldValue+"</p></li>"
                                                + "</ul>");
                                                $("#ul"+k).addClass("actions");
                                            }

                                            else
                                            {
                                                if(editableField.editorType == "BOOLEAN")
                                                {
                                                    var inputId = "input" + k;
                                                    var sliderId = "slider" + k;
                                                    var insetId = "inset" + k;
                                                    var controlId = "control" + k;


                                                    $("#fieldList").append("<ul id='ul"+k+"'>"
                                                    + "<li><label>"+editableField.displayName+"</label></li>"
                                                    + "<li><div onclick='sliderClicked("+JSON.stringify(sliderId)+")' id="+JSON.stringify(sliderId)+"><div id="+JSON.stringify(insetId)+"><div id="+JSON.stringify(controlId)+"></div></div></div></li>"
                                                    + "<li><a onclick='postChangesBoolean("+JSON.stringify(editableField.fieldName)+", "+JSON.stringify(sliderId)+")' class='button big special'>Save</a></li>"
                                                    + "</ul>");
                                                    $("#ul"+k).addClass("actions");

                                                    if(editableField.fieldValue == true)
                                                    {
                                                      $("#"+sliderId).addClass("bool-slider true");
                                                    }
                                                    else
                                                    {
                                                      $("#"+sliderId).addClass("bool-slider false");
                                                    }

                                                     $("#"+insetId).addClass("inset");
                                                     $("#"+controlId).addClass("control");
                                                }

                                                else
                                                {
                                                    var inputId = "input" + k;
                                                    $("#fieldList").append("<ul id='ul"+k+"'>"
                                                    + "<li><label>"+editableField.displayName+"</label></li>"
                                                    + "<li><input id="+JSON.stringify(inputId)+" type='text' value='"+editableField.fieldValue+"'</input></li>"
                                                    + "<li><a onclick='postChanges("+JSON.stringify(editableField.fieldName)+", document.getElementById("+JSON.stringify(inputId)+").value)' class='button big special'>Save</a></li>"
                                                    + "</ul>");
                                                    $("#ul"+k).addClass("actions");
                                                }
                                            }
                                        }
                                     }
                                }
                             }
                         }
                         else
                         {
                             document.getElementById("info").innerText = "Server error::"+resolveErrorType(responseData.error_message);
                         }
                         },
                         error: function(xhr, status, error) {
                             document.getElementById("info").innerText = "Error communicating with server: " + status + "::" + xhr.responseText;
                         }
                     });
          }

    function sliderClicked(id)
    {
        if($("#"+id).hasClass("true"))
        {
             $("#"+id).removeClass("true").addClass("false");
        }
        else
        {
             $("#"+id).removeClass("false").addClass("true");
        }
    }



    function editBlock(element)
    {
        var url = "editblock.html?secretKey=" + encodeURIComponent(getUrlParameter("secretKey")) + "&internalName="+element.id;
        window.location.href = url;
    }

     function postChangesBoolean(fieldName, sliderId)
        {
            var slider = $("#"+sliderId).hasClass("true");

            var baseServerUrl="http://"+location.hostname;
            var coreCoreUrl=baseServerUrl+":777/undercurrentcore/core";
            var secretKey = getUrlParameter("secretKey");
            var jsonToPost = JSON.stringify({"data":[{"internalName": getUrlParameter("internalName"), "editedData":[{"fieldName":fieldName,"fieldValue":slider}]}]})

                    $.ajax({
                        url: coreCoreUrl+"?secretKey="+ secretKey,
                        type: "POST",
                        data:jsonToPost,
                        dataType: "json",
                        cache: false,
                        contentType: false,
                        success: function(responseData) {

                                if(responseData.status == true)
                                {

                                }
                                else
                                {
                                    document.getElementById("info").innerText = "Server error::"+resolveErrorType(responseData.error_message);
                                }
                                },
                        error: function(xhr, status, error) {
                                    document.getElementById("info").innerText = "Error communicating with server: " + status + "::" + xhr.responseText;
                                }
                            });
        }

    function postChanges(fieldName, fieldValue)
    {

        var baseServerUrl="http://"+location.hostname;
        var coreCoreUrl=baseServerUrl+":777/undercurrentcore/core";
        var secretKey = getUrlParameter("secretKey");
        var jsonToPost = JSON.stringify({"data":[{"internalName": getUrlParameter("internalName"), "editedData":[{"fieldName":fieldName,"fieldValue":fieldValue}]}]})

                $.ajax({
                    url: coreCoreUrl+"?secretKey="+ secretKey,
                    type: "POST",
                    data:jsonToPost,
                    dataType: "json",
                    cache: false,
                    contentType: false,
                    success: function(responseData) {

                            if(responseData.status == true)
                            {

                            }
                            else
                            {
                                document.getElementById("info").innerText = "Server error::"+resolveErrorType(responseData.error_message);
                            }
                            },
                    error: function(xhr, status, error) {
                                document.getElementById("info").innerText = "Error communicating with server: " + status + "::" + xhr.responseText;
                            }
                        });
    }

    function getBlocks(rawData)
    {
        return rawData.data;
    }

    function countBlocks(rawData)
    {
        return rawData.data.length;
    }

    function getBlockByIndex(rawData, index)
    {
        return rawData.data[index];
    }

    function getBlockCollections(rawData, index)
    {
        return rawData.data[index].editableFields[0].collections;
    }

    function resolveErrorType(errorResponse)
    {

    if(errorResponse == "EMPTY_REQUEST_PARAMETER")
    {
    return "Empty request parameter sent.";
    }

    if(errorResponse == "SERVER_ERROR")
    {
    return "Internal server error.";
    }

    if(errorResponse == "WORLD_TE_DOES_NOT_EXIST")
    {
    return "Referenced TileEntity does not exist.";
    }

    if(errorResponse == "USER_NOT_REGISTERED")
    {
    return "Specified secret key is not registered on this server.";
    }

    if(errorResponse == "CANT_RETRIEVE_USER_INFO")
    {
    return "Can't retrieve player information. UnderCurrent data may be corrupt.";
    }

    if(errorResponse == "CANT_USE_FIELD")
    {
    return "Editable field class other than the supported casting types.";
    }

    if(errorResponse == "CANT_DO_TE_SWOP")
    {
    return "Tile entity field reflection failed.";
    }

    if(errorResponse == "NO_BLOCK_FOUND_FOR_INTERNAL_NAME")
    {
    return "Block internal name that was specified does not exist on this server for you.";
    }

    if(errorResponse == "ERROR_GETTING_TILE_UCTILEDEF")
    {
    return "Error getting the specified block's tile definition. Please contact the block's mod author.";
    }

    if(errorResponse == "TE_NOT_IUCTILE")
    {
    return "Specified block is not an UnderCurrent tile. Please contact the block's mod author.";
    }

    if(errorResponse == "UCTILEDEF_IS_NULL")
    {
    return "Specified block's tile definition returned null. Please contact the block's mod author.";
    }

    return errorResponse;

    }

    function getUrlParameter(sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    };


