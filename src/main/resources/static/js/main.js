$(document).ready(function () {

    // GET REQUEST
    $("#getAllProduct").click(function (event) {
        event.preventDefault();
        ajaxGet();
    });


    // DO GET
    function ajaxGet() {
        $.ajax({
            type: "GET",
            url: window.location + "/hot/result",
            success: function (result) {
                if (result.status == "Done") {
                    $('#getResultDiv ul').empty();
                    var custList = "";
                    $.each(result.data, function (i, topProduct) {
                        var topBook = "Best Seller Product: " + "<br>"
                            + "code: " + topProduct.code + "<br>"
                            + "name: " + topProduct.name + "<br>"
                            + "price: " + topProduct.price + "<br>"
                            + "----------------------------------------------"
                            + "<br>";
                        var image = "/productImage?code=" + topProduct.code;
                        //var image = "http://localhost:8080/productImage?code=S001"
                        // $('#getResultDiv .list-group').append(topProduct)
                        $('#getResultDiv .list-group').html(topBook);
                        $("#profileImage").attr('src', image);
                    });
                    console.log("Success: ", result);
                } else {
                    $("#getResultDiv").html("<strong>Error</strong>");
                    console.log("Fail: ", result);
                }
            },
            error: function (e) {
                $("#getResultDiv").html("<strong>Error</strong>");
                console.log("ERROR: ", e);
            }
        });
    }
});