    function resolveErrorType(errorResponse) {

         if (errorResponse == "EMPTY_REQUEST_PARAMETER") {
             return "Empty request parameter sent.";
         }

         if (errorResponse == "SERVER_ERROR") {
             return "Internal server error.";
         }

         if (errorResponse == "WORLD_TE_DOES_NOT_EXIST") {
             return "Referenced TileEntity does not exist.";
         }

         if (errorResponse == "USER_NOT_REGISTERED") {
             return "Specified secret key is not registered on this server.";
         }

         if (errorResponse == "CANT_RETRIEVE_USER_INFO") {
             return "Can't retrieve player information. UnderCurrent data may be corrupt.";
         }

         if (errorResponse == "CANT_USE_FIELD") {
             return "Editable field class other than the supported casting types.";
         }

         if (errorResponse == "CANT_DO_TE_SWOP") {
             return "Tile entity field reflection failed.";
         }

         if (errorResponse == "NO_BLOCK_FOUND_FOR_INTERNAL_NAME") {
             return "Block internal name that was specified does not exist on this server for you.";
         }

         if (errorResponse == "ERROR_GETTING_TILE_UCTILEDEF") {
             return "Error getting the specified block's tile definition. Please contact the block's mod author.";
         }

         if (errorResponse == "TE_NOT_IUCTILE") {
             return "Specified block is not an UnderCurrent tile. Please contact the block's mod author.";
         }

         if (errorResponse == "UCTILEDEF_IS_NULL") {
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


