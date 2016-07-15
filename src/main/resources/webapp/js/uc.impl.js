function popup()
    {
       window.alert("f1 called");
       window.alert(document.getElementById("secretKey").value);
    }

function authAndLogin(){
       window.alert(document.getElementById("secretKey").value);
       $.ajax({
               url: "http://localhost:777/undercurrentcore/auth?secretKey="+ document.getElementById("secretKey").value,
               type: "GET",
               dataType: "json",
               success: function(responseData) {
                   window.alert(responseData);
               }
           });
    }
