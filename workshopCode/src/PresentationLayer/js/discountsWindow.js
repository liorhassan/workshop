function addNewDiscount(data) {
    data.storeName = document.getElementById("storeName").value;
    fetch("http://localhost:8080/tradingSystem/addDiscount", {
        method: "POST",
        body: JSON.stringify(data)
    })
        .then(response => {
            return response.json();
        })
        .then(data => {
            console.log(data);
        })
}

function addBasketAmount() {
    const amount = document.getElementById("basketAmountNumber").value;
    const data = {
        type: "onBasket",
        subType: "onProductsAmount",
        amount: amount
    }
    addNewDiscount(data);
}

function addBasketPrice() {
    const price = document.getElementById("basketPriceNumber").value;
    const data = {
        type: "onBasket",
        subType: "onCost",
        price: price
    }
    addNewDiscount(data);
}

function addProductReveal() {
    const name = document.getElementById("revealedProductName").value;
    const percents = document.getElementById("revealedDiscountPercents").value;

    const data = {
        type: "onProduct",
        subType: "revealed",
        productName: name,
        percents: percents
    }

    addNewDiscount(data);
}

function addProductConditional() {
    const name = document.getElementById("conditionalProductName").value;
    const amount = document.getElementById("conditionalAmount").value
    const percents = document.getElementById("conditionalDiscountPercents").value;
    const radioBtn = document.getElementsByName("productCondition");

    const data = {
        type: "onProduct",
        subType: "conditional",
        productName: name,
        amount: amount,
        percents: percents,
        onProduct: radioBtn[0].checked ? true : false,
        onNextProduct: radioBtn[1].checked ? true : false,
    }
    console.log(data);


    // addNewDiscount(data);
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