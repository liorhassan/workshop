function addNewPolicy(data) {
    data.store = document.getElementById("storeName").value;
    fetch("http://localhost:8080/tradingSystem/addPurchasePolicy", {
        method: "POST",
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(data => {
            if (data.SUCCESS) {
                Swal.fire(
                    'Congratulations!',
                    data.SUCCESS,
                    'success')
            } else {
                Swal.fire(
                    'OOPS...',
                    data,
                    'error')
            }
        })
}

function addPolicyByProduct() {
    const name = document.getElementById("productName").value;
    const amount = document.getElementById("amount").value;
    const radioBtn = document.getElementsByName("minOrMaxProduct");

    const data = {
        type: "simple",
        subtype: "PurchasePolicyProduct",
        productName: name,
        amount: amount,
        minOrMax: radioBtn[0].checked ? false : true,
    }

    addNewPolicy(data);
}

function addStorePolicy() {
    const limit = document.getElementById("limit").value;
    const radioBtn = document.getElementsByName("minOrMaxStore");


    const data = {
        type: "simple",
//        subtype: "DiscountCondBasketProdcts",
        subtype: "PurchasePolicyStore",
        limit: limit,
        minOrMax: radioBtn[0].checked ? false : true,
    }

    addNewPolicy(data);
}

function resetActiveTabsButThis(clickedTab) {
    if (!clickedTab.classList.contains("active")) {
        const allActive = document.getElementsByClassName("active")
        const relevantActive = Array.prototype.filter.call(allActive, function (currElement) {
            return currElement.getAttribute("name") != 'menu-nav';
        });

        for (let i = 0; i < relevantActive.length; i++) {
            const element = relevantActive[i];

            if (element != clickedTab && element.getAttribute("name") != "") {
                element.classList.remove("active")
            }
        }

        const allShown = document.getElementsByClassName("show")
        while (allShown.length > 0) {
            const element = allShown[0];

            if (element != clickedTab) {
                element.classList.remove("show")
            }
        }
    }
}

function setCompose(clickedTab) {
    resetActiveTabsButThis(clickedTab);
    createCompStatement(1,0,'Root')
}