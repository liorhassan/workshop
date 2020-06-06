const window_name = "ManageMyStores";
var activeStore;
var activeProducts;
document.addEventListener("DOMContentLoaded", function () {

    fetch("http://localhost:8080/tradingSystem/isLoggedIn")
    .then(response=>response.json())
    .then(updateNavBar);
    


     fetch("http://localhost:8080/tradingSystem/myStores")
         .then(response => response.json())
          .then(setMyStores)

    document.getElementById("new-store-button").addEventListener("click",function(){
        showPopUp("Open Store");
    });

    document.getElementById("editPermBtn").addEventListener("click",function(){
        store_name = activeStore.name;
        user_name = document.getElementById("perm-username").value;
        permissions = []
        if(document.getElementById("manageSupplyPermOpt").checked)
            permissions.push("Manage Supply")
        if(document.getElementById("viewHistoryPermOpt").checked)
            permissions.push("View Purchasing History")
        if(document.getElementById("addDiscPermOpt").checked)
            permissions.push("Add New Discount")
        if(document.getElementById("addperchPolPermOpt").checked)
            permissions.push("Add New Purchase Policy")
        var sfy = JSON.stringify({user: user_name, store: store_name, permission: permissions});
        fetch("http://localhost:8080/tradingSystem/editPermission", {
            method: "POST",
            body: JSON.stringify({user: user_name, store: store_name, permission: permissions})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        });
        document.getElementById("perm-username").value = "";
        document.getElementById("manageSupplyPermOpt").checked = false;
        document.getElementById("viewHistoryPermOpt").checked = false;
        document.getElementById("addDiscPermOpt").checked = false;
        document.getElementById("addDiscPolPermOpt").checked = false;
        document.getElementById("editPermissionsModal").style.display = "none";
        
    })

    initOpenStoreModel();
    
    initAddManagerModel();

    initAddOwnerModel();

    initRemoveManagerModel();

    initRemoveOwnerModel();

    initManageSupplyModel();
    

    fetch("http://localhost:8080/tradingSystem/allCategories")
         .then(response => response.json())
         .then(setCategories)

    // When the user clicks on <span> (x), close the modal
    var x_buttons = document.getElementsByClassName("close");
    for (let x_button of x_buttons) {
        x_button.addEventListener("click", function () {
            var modals = document.getElementsByClassName("modal");
            for (let modal of modals) {
                modal.style.display = "none";
            }
        });
    }
});

function initOpenStoreModel() {   
    document.getElementById("confirm-open-store-btn").addEventListener("click",function(){
        var store_name = document.getElementById("StoreNameInput").value;
        var store_description  = document.getElementById("StoreDescInput").value;
        fetch("http://localhost:8080/tradingSystem/openNewStore", {
            method: "POST",
            body: JSON.stringify({store: store_name, description: store_description})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })
        fetch("http://localhost:8080/tradingSystem/myStores")
         .then(response => response.json())
          .then(setMyStores);
        document.getElementById("StoreNameInput").value = "";
        document.getElementById("StoreDescInput").value = "";
        document.getElementById("openStoreModal").style.display = "none";
        
    })
}

function initAddManagerModel() {   
    document.getElementById("add-manager-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("new-manager-name").value;
        fetch("http://localhost:8080/tradingSystem/addStoreManager", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })
        document.getElementById("new-manager-name").value = "";
        document.getElementById("addManagerModal").style.display = "none";
    })
}

function initAddOwnerModel() {   
    document.getElementById("add-owner-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("new-owner-name").value;
        fetch("http://localhost:8080/tradingSystem/addStoreOwner", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })

        document.getElementById("new-owner-name").value = "";
        document.getElementById("addOwnerModal").style.display = "none";
    })
}

function initRemoveManagerModel() {   
    document.getElementById("remove-manager-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("remove-manager-name").value;
        fetch("http://localhost:8080/tradingSystem/removeStoreManager", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })

        document.getElementById("remove-manager-name").value = "";
        document.getElementById("removeManagerModal").style.display = "none";
    })
}

function initRemoveOwnerModel() {   
    document.getElementById("remove-owner-button").addEventListener("click",function(){
        var store_name = activeStore.name;
        var username  = document.getElementById("remove-owner-name").value;
        fetch("http://localhost:8080/tradingSystem/removeStoreOwner", {
            method: "POST",
            body: JSON.stringify( {user: username, store:store_name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })

        document.getElementById("remove-owner-name").value = "";
        document.getElementById("removeOwnerModal").style.display = "none";
    })
}

function initManageSupplyModel(){
    document.getElementById("edited-product-name").addEventListener("blur",updateProoductSupplyField);
    document.getElementById("updateSpplyBtn").addEventListener("click",function(){
        var prod_name = document.getElementById("edited-product-name").value;
        var prod_price = document.getElementById("update-product-price").value;
        var prod_cat = document.getElementById("update-product-category").value;
        var prod_desc = document.getElementById("update-store-desc").value;
        var prod_amount = document.getElementById("update-product-amount").value;
        fetch("http://localhost:8080/tradingSystem/updateInventory", {
            method: "POST",
            body: JSON.stringify( {store:activeStore.name, product: prod_name, price:prod_price, category:prod_cat, desc:prod_desc, amount:prod_amount})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then((responseMsg) => {
            if (responseMsg.SUCCESS) {
                Swal.fire(
                      'SUCCESS!',
                      responseMsg.SUCCESS,
                      'success')
            } else {
                Swal.fire(
                   'OOPS!',
                   responseMsg,
                   'error')
            }
        })
        document.getElementById("edited-product-name").value = "";
        document.getElementById("update-product-price").value = "";
        document.getElementById("update-product-category").value = "";
        document.getElementById("update-store-desc").value = "";
        document.getElementById("update-product-amount").value = "";
        document.getElementById("manageSupplyModal").style.display = "none";
        
    })
}

function updateSupplyProducts(products){
    activeProducts = products;
    var option_menu = document.getElementById("store-product-list");
    option_menu.innerHTML = "";
    products.forEach(prod=>{
        const option = document.createElement("option");
        option.value = prod.name;
        option.append(document.createTextNode(prod.name));
        option_menu.appendChild(option);
    })
}

function updatePurchaseHistory(history){
    var historyList = document.getElementById("history-list");
    historyList.innerHTML = "<br>";
    //history.innerHTML = "";
    history.forEach(purchase=>{
        purchase.forEach(prod=>{
            const element = document.createElement("li");
            element.classList.add("list-group-item");
            element.classList.add("d-flex");
            element.classList.add("justify-content-between");
            element.classList.add("lh-condensed");
            const element_div = document.createElement("div");
            const product_name = document.createElement("h6");
            product_name.classList.add("my-0");
            product_name.append(document.createTextNode(prod.name));
            const amount = document.createElement("small");
            amount.classList.add("text-muted");
            amount.append(document.createTextNode("Amount: "+prod.amount));
            element_div.appendChild(product_name);
            element_div.appendChild(amount);
            const price = document.createElement("span");
            price.classList.add("text-muted");
            price.append(document.createTextNode("$" + prod.price));
            element.appendChild(element_div);
            element.appendChild(price); 
            historyList.appendChild(element);
        })
        const break_element = document.createElement("br");
        historyList.appendChild(break_element);
    })
}

function updateCandidatesWindow(candidates){
    var candidatesList = document.getElementById("candidates-list");
    candidatesList.innerHTML = "";
    candidates.forEach(cand=>{
        const element = document.createElement("li");
        element.classList.add("list-group-item");
        element.classList.add("d-flex");
        element.classList.add("justify-content-between");
        element.classList.add("lh-condensed");
        const element_div = document.createElement("div");
        const cand_name = document.createElement("h6");
        cand_name.classList.add("my-0");
        cand_name.append(document.createTextNode(cand.name));
        element_div.appendChild(cand_name);
        const app_button = document.createElement("button");
        app_button.classList.add("btn");
        app_button.classList.add("btn-primary");
        app_button.classList.add("cand-btn");
        app_button.append(document.createTextNode("Approve"));
        app_button.addEventListener("click",function(){
            fetch("http://localhost:8080/tradingSystem/approveCandidate", {
                method: "POST",
                body: JSON.stringify({user: user_name, store: store_name, status: "approve"})
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text();
                }
            })
            .then((responseMsg) => {
                if (responseMsg.SUCCESS) {
                    Swal.fire(
                        'SUCCESS!',
                        responseMsg.SUCCESS,
                        'success');
                        
                    updateCandidates();
                } else {
                    Swal.fire(
                    'OOPS!',
                    responseMsg,
                    'error')
                }
            })
        });
        const rej_button = document.createElement("button");
        rej_button.classList.add("btn");
        rej_button.classList.add("btn-primary");
        rej_button.append(document.createTextNode("Reject"));
        rej_button.addEventListener("click",function(){
            fetch("http://localhost:8080/tradingSystem/approveCandidate", {
                method: "POST",
                body: JSON.stringify({user: user_name, store: store_name, status: "reject"})
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text();
                }
            })
            .then((responseMsg) => {
                if (responseMsg.SUCCESS) {
                    Swal.fire(
                        'SUCCESS!',
                        responseMsg.SUCCESS,
                        'success');
                        
                    updateCandidates();
                } else {
                    Swal.fire(
                    'OOPS!',
                    responseMsg,
                    'error')
                }
            })
        })
        const app_span = document.createElement("span");
        app_span.appendChild(app_button);
        app_span.appendChild(rej_button);
        element.appendChild(element_div);
        element.appendChild(app_span); 
        candidatesList.appendChild(element);
    })
}

function setMyStores(stores) {
    const numOfStores = document.getElementById("num-of-stores");
    numOfStores.innerHTML = "";
    numOfStores.append(document.createTextNode(stores.length));

    const stores_list = document.getElementById("stores-list");

    stores_list.innerHTML = "";

    stores.forEach(currStore => {
        const element = document.createElement("li");
        element.classList.add("list-group-item");
        element.classList.add("d-flex");
        element.classList.add("justify-content-between");
        element.classList.add("lh-condensed");
        
        const element_div = document.createElement("div");
        
        const store_name = document.createElement("h6");
        store_name.classList.add("my-0");
        store_name.append(document.createTextNode(currStore.name));

        const title = document.createElement("small");
        title.classList.add("text-muted");
        title.append(document.createTextNode(currStore.type));

        element_div.appendChild(store_name);
        element_div.appendChild(title);

        const actions = document.createElement("span");

        const action_div = document.createElement("div");
        action_div.classList.add("input-group-append");

        const action_button = document.createElement("button");
        action_button.append(document.createTextNode("Actions"));
        action_button.classList.add("btn");
        action_button.classList.add("btn-outline-secondary");
        action_button.classList.add("dropdown-toggle");
        action_button.type = "button";
        action_button.setAttribute("data-toggle", "dropdown");
        action_button.setAttribute("aria-haspopup", "true");
        action_button.setAttribute("aria-expanded", "false");

        const action_dropdown = document.createElement("div");

        action_dropdown.classList.add("dropdown-menu");

        currStore.options.forEach(currAction=>{
            const a = document.createElement("a");
            a.addEventListener("click",function(){
            activeStore = currStore;
            showPopUp(currAction);
            })
            a.classList.add("dropdown-item");
            a.append(document.createTextNode(currAction));

            action_dropdown.appendChild(a);  
        })

        action_div.appendChild(action_button);
        action_div.appendChild(action_dropdown);
        actions.appendChild(action_div);
        
        element.appendChild(element_div);
        element.appendChild(actions);
        
        stores_list.appendChild(element);
    })
}


function setCategories(categories) {

    const category_drop1 = document.getElementById("update-product-category");
    category_drop1.innerHTML = "";

    const main_item1 = document.createElement("option");
    main_item1.selected = true;
    main_item1.append(document.createTextNode("Category"));
    category_drop1.appendChild(main_item1);

    categories.forEach(currCat=>{
        const item1 = document.createElement("option");
        item1.value=currCat;
        item1.append(document.createTextNode(currCat));
        category_drop1.appendChild(item1);
    })
}

function updateCandidates() {
    fetch("http://localhost:8080/tradingSystem/newOwnerCandidates", {
            method: "POST",
            body: JSON.stringify({store:activeStore.name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(updateCandidatesWindow)
}


actionToModel={
    "Open Store":"openStoreModal",
    "Add Manager":"addManagerModal",
    "Add Owner":"addOwnerModal",
    "Remove Manager":"removeManagerModal",
    "Remove Owner":"removeOwnerModal",
    "Edit Permissions":"editPermissionsModal",
    "Manage Supply":"manageSupplyModal",
    "View Purchasing History":"viewPurchaseHistoryModal",
    "Approve New Owner":"approveNewOwnerModal"
}

function showPopUp(action){
    if(action == "Approve New Owner"){
        updateCandidates();
    }
    if(action == "View Purchasing History"){
        fetch("http://localhost:8080/tradingSystem/storePurchaseHistory", {
            method: "POST",
            body: JSON.stringify({store:activeStore.name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(updatePurchaseHistory)
    }
    if(action == "Manage Supply"){
        fetch("http://localhost:8080/tradingSystem/getStoreProducts", {
            method: "POST",
            body: JSON.stringify({store:activeStore.name})
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(updateSupplyProducts)
    }
    if(action == "Add New Discount")
        window.location.href = "http://localhost:8080/html/discountsWindow.html";

    if(action == "Add new Purchase Policy")
            window.location.href = "http://localhost:8080/html/PolicyWindow.html";

    document.getElementById(actionToModel[action]).style.display = "block";
}

function updateProoductSupplyField(){
    var prod_name = document.getElementById("edited-product-name").value;
    for( let prod of activeProducts){
        if(prod.name == prod_name){
            document.getElementById("update-product-price").value = prod.price;
            document.getElementById("update-product-category").value = prod.category;
            document.getElementById("update-store-desc").value = prod.description;
            document.getElementById("update-product-amount").value = prod.amount;
        }
    }
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    var models = this.document.getElementsByClassName("modal");
    for(let model of models){
        if(model == event.target)
            model.style.display = "none";
    }
}




