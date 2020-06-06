const window_name = "PolicyView";
document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/tradingSystem/isLoggedIn")
    .then(response=>response.json())
    .then(updateNavBar);
})

const CATEGORY = "category"
const PRODUCT = "product"
const COMP = "composition"
const IF = "if"

let result = {};
let rootElementType;
let totalComp;
let prevSelect = {}
let allDiscounts;
function getDiscounts() {
    const store = document.getElementById("storeName").value;
    fetch("http://localhost:8080/tradingSystem/getDiscounts", {
        method: "POST",
        body: JSON.stringify({ store: store })
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(data => {
            if (typeof (data) == "string") {
                Swal.fire(
                    'OOPS..',
                    data,
                    'error')

            } else if (data.length == 0) {
                Swal.fire(
                    'Unfortunatly',
                    "There are no discounts",
                    'warning')
            } else {
                allDiscounts = data;
                document.getElementById("allPolicyDiv").style.display = 'inline';
            }
        })
}

function submitPolicy() {
    result = {}
    if (rootElementType == COMP) {
        result.type = "Comp";
        const rator = document.getElementById("rator" + 1)
        result.operator = rator.options[rator.selectedIndex].value;
        const rand1 = document.getElementById(COMP + 1 + "rand1")
        result.operand1 = getOperand(rand1.options[rand1.selectedIndex].value, rand1.dataset.child)
        const rand2 = document.getElementById(COMP + 1 + "rand2")
        result.operand2 = getOperand(rand2.options[rand2.selectedIndex].value, rand2.dataset.child)
    } else {
        result.type = "If";
        const thenElem = document.getElementById("then" + 1)
        result.discountId = thenElem.options[thenElem.selectedIndex].value;
        const ifElem = document.getElementById("if" + 1)
        result.condition = getOperand(ifElem.options[ifElem.selectedIndex].value, ifElem.dataset.child)
    }

    result.store = document.getElementById("storeName").value;

    fetch("http://localhost:8080/tradingSystem/addDiscountPolicy", {
        method: "POST",
        body: JSON.stringify(result)
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return response.text();
        }
    }).then(data => {
        if (data.SUCCESS) {
            Swal.fire(
                'Congratulations!',
                data.SUCCESS,
                'success').then(() => {
                    window.location.href = "http://localhost:8080/html/ManageMyStores.html";
                })
        } else {
            Swal.fire(
                'OOPS...',
                data,
                'error')
        }
    })
}

function getOperand(type, childIndex) {
    const currOperand = {};
    switch (type) {
        case CATEGORY: {
            currOperand.type = "simpleCategory"
            const categoryElem = document.getElementById(CATEGORY + childIndex);
            currOperand.category = categoryElem.value;
            break;
        }
        case PRODUCT: {
            currOperand.type = "simpleProduct"
            const productElem = document.getElementById(PRODUCT + childIndex);
            currOperand.productName = productElem.value;
            break;
        }
        case COMP: {
            currOperand.type = "Comp";
            const rator = document.getElementById("rator" + childIndex)
            currOperand.operator = rator.options[rator.selectedIndex].value;
            const rand1 = document.getElementById(COMP + childIndex + "rand1")
            currOperand.operand1 = getOperand(rand1.options[rand1.selectedIndex].value, rand1.dataset.child)
            const rand2 = document.getElementById(COMP + childIndex + "rand2")
            currOperand.operand2 = getOperand(rand2.options[rand2.selectedIndex].value, rand2.dataset.child)

            break;
        }
        case IF: {
            currOperand.type = "If";
            const thenElem = document.getElementById("then" + childIndex)
            currOperand.discountId = thenElem.options[thenElem.selectedIndex].value;
            const ifElem = document.getElementById("if" + childIndex)
            currOperand.condition = getOperand(ifElem.options[ifElem.selectedIndex].value, ifElem.dataset.child)

            break;
        }
    }

    return currOperand;
}

function createCompStatement(compCounter, parentIndex, parentID) {
    if (parentID == "Root") {
        document.getElementById("policy-content").innerHTML = "";
        rootElementType = COMP;
        totalComp = compCounter;
    }
    if (compCounter != 1 || document.getElementById("compositionDiv1") == null) {
        const compDiv = document.createElement("div");
        compDiv.setAttribute("id", COMP + "Div" + compCounter);
        compDiv.className = "policyDiv input-group mb-3";

        // Add label header
        const labelHeader = document.createElement("label");
        labelHeader.append(document.createTextNode("Composition #" + compCounter + " of " + parentID))
        compDiv.appendChild(labelHeader);

        const contentDiv = document.createElement("div");
        contentDiv.className = "input-group mb-3";
        compDiv.appendChild(contentDiv);

        // Create content
        // operator
        const ratorDiv = document.createElement("div");
        ratorDiv.className = "input-group-prepend";
        const ratorTextSpan = document.createElement("span");
        ratorTextSpan.className = "input-group-text";
        ratorTextSpan.append(document.createTextNode("rator"))
        ratorDiv.appendChild(ratorTextSpan);
        contentDiv.appendChild(ratorDiv);

        const ratorinput = document.createElement("select");
        ratorinput.setAttribute("id", "rator" + compCounter);

        const ratorOption1 = document.createElement("option");
        ratorOption1.setAttribute("value", "AND");
        ratorOption1.appendChild(document.createTextNode("AND"));
        const ratorOption2 = document.createElement("option");
        ratorOption2.setAttribute("value", "OR");
        ratorOption2.appendChild(document.createTextNode("OR"));
        const ratorOption3 = document.createElement("option");
        ratorOption3.setAttribute("value", "XOR");
        ratorOption3.appendChild(document.createTextNode("XOR"));

        ratorinput.appendChild(ratorOption1)
        ratorinput.appendChild(ratorOption2)
        ratorinput.appendChild(ratorOption3)
        contentDiv.appendChild(ratorinput);

        // rand 1
        const rand1Div = document.createElement("div");
        rand1Div.className = "input-group-prepend";
        const rand1TextSpan = document.createElement("span");
        rand1TextSpan.className = "input-group-text";
        rand1TextSpan.append(document.createTextNode("rand1"))
        rand1Div.appendChild(rand1TextSpan);
        contentDiv.appendChild(rand1Div);

        const rand1Select = document.createElement("select");
        rand1Select.setAttribute("id", COMP + compCounter + "rand1");
        rand1Select.setAttribute("name", "rand1");
        rand1Select.dataset.parent = parentIndex;
        rand1Select.dataset.counter = compCounter;

        const rand1DefaultOption = document.createElement("option");
        rand1DefaultOption.setAttribute("selected", true)
        rand1DefaultOption.appendChild(document.createTextNode("- Choose operand -"));
        const rand1Option1 = document.createElement("option");
        rand1Option1.setAttribute("value", CATEGORY);
        rand1Option1.appendChild(document.createTextNode(CATEGORY));
        const rand1Option2 = document.createElement("option");
        rand1Option2.setAttribute("value", PRODUCT);
        rand1Option2.appendChild(document.createTextNode(PRODUCT));
        const rand1Option3 = document.createElement("option");
        rand1Option3.setAttribute("value", COMP);
        rand1Option3.appendChild(document.createTextNode(COMP));
        const rand1Option4 = document.createElement("option");
        rand1Option4.setAttribute("value", IF);
        rand1Option4.appendChild(document.createTextNode(IF));

        rand1Select.appendChild(rand1DefaultOption);
        rand1Select.appendChild(rand1Option1);
        rand1Select.appendChild(rand1Option2);
        rand1Select.appendChild(rand1Option3);
        rand1Select.appendChild(rand1Option4);

        rand1Select.addEventListener("focus", (elem) => {
            prevSelect[elem.target.id] = elem.target.value
        })

        rand1Select.addEventListener("change", (elem) => {
            operandTypeChanged(elem.target.value, prevSelect[elem.target.id], elem.target.dataset.counter, elem.target.id);
            prevSelect[elem.target.id] = elem.target.value
        })

        contentDiv.appendChild(rand1Select);

        // rand 2
        const rand2Div = document.createElement("div");
        rand2Div.className = "input-group-prepend";
        const rand2TextSpan = document.createElement("span");
        rand2TextSpan.className = "input-group-text";
        rand2TextSpan.append(document.createTextNode("rand2"))
        rand2Div.appendChild(rand2TextSpan);
        contentDiv.appendChild(rand2Div);

        const rand2Select = document.createElement("select");
        rand2Select.setAttribute("id", COMP + compCounter + "rand2");
        rand2Select.setAttribute("name", "rand2");
        rand2Select.dataset.parent = parentIndex;
        rand2Select.dataset.counter = compCounter;

        const rand2DefaultOption = document.createElement("option");
        rand2DefaultOption.setAttribute("selected", true)
        rand2DefaultOption.appendChild(document.createTextNode("- Choose operand -"));
        const rand2Option1 = document.createElement("option");
        rand2Option1.setAttribute("value", CATEGORY);
        rand2Option1.appendChild(document.createTextNode(CATEGORY));
        const rand2Option2 = document.createElement("option");
        rand2Option2.setAttribute("value", PRODUCT);
        rand2Option2.appendChild(document.createTextNode(PRODUCT));
        const rand2Option3 = document.createElement("option");
        rand2Option3.setAttribute("value", COMP);
        rand2Option3.appendChild(document.createTextNode(COMP));
        const rand2Option4 = document.createElement("option");
        rand2Option4.setAttribute("value", IF);
        rand2Option4.appendChild(document.createTextNode(IF));

        rand2Select.appendChild(rand2DefaultOption);
        rand2Select.appendChild(rand2Option1);
        rand2Select.appendChild(rand2Option2);
        rand2Select.appendChild(rand2Option3);
        rand2Select.appendChild(rand2Option4);

        rand2Select.addEventListener("focus", (elem) => {
            prevSelect[elem.target.id] = elem.target.value
        })

        rand2Select.addEventListener("change", (elem) => {
            operandTypeChanged(elem.target.value, prevSelect[elem.target.id], elem.target.dataset.counter, elem.target.id);
            prevSelect[elem.target.id] = elem.target.value
        })

        contentDiv.appendChild(rand2Select);

        document.getElementById("policy-content").appendChild(compDiv);

        totalComp++
    }
}

function operandTypeChanged(operandType, prevType, compIndex, parentID) {
    if (prevType != operandType) {
        const parentIndex = document.getElementById(parentID).dataset.child;
        const currElem = document.getElementById(prevType + "Div" + parentIndex);
        if (currElem && prevType != "- Choose operand -") {
            removeDescendants(prevType, parentIndex)
            // currElem.parentElement.removeChild(currElem);
            //  currElem.dataset.child
        }
        document.getElementById(parentID).dataset.child = totalComp;
        document.getElementById(parentID).dataset.childType = operandType;

        switch (operandType) {
            case COMP: {
                createCompStatement(totalComp, compIndex, parentID);
                break;
            }
            case PRODUCT: {
                createProductStatement(totalComp, compIndex, parentID);

                break;
            }
            case CATEGORY: {
                createCategoryrStatement(totalComp, compIndex, parentID);

                break;
            }
            case IF: {
                createIfStatement(totalComp, compIndex, parentID);

                break;
            }
            default: {

                break;
            }
        }
    }
}

function createIfStatement(compCounter, parentIndex, parentID) {
    if (parentID == "Root") {
        document.getElementById("policy-content").innerHTML = "";
        rootElementType = IF;
        totalComp = compCounter;
    }

    const ifDiv = document.createElement("div");
    ifDiv.setAttribute("id", IF + "Div" + compCounter);
    ifDiv.className = "policyDiv input-group mb-3";

    // Add label header
    const labelHeader = document.createElement("label");
    labelHeader.append(document.createTextNode("Composition #" + compCounter + " of " + parentID))
    ifDiv.appendChild(labelHeader);

    const contentDiv = document.createElement("div");
    contentDiv.className = "input-group mb-3";
    ifDiv.appendChild(contentDiv);

    // If
    const innedrIfDiv = document.createElement("div");
    innedrIfDiv.className = "input-group-prepend";
    const ifTextSpan = document.createElement("span");
    ifTextSpan.className = "input-group-text";
    ifTextSpan.append(document.createTextNode("If"))
    innedrIfDiv.appendChild(ifTextSpan);
    contentDiv.appendChild(innedrIfDiv);

    const ifType = document.createElement("select")
    ifType.setAttribute("id", "if" + compCounter)
    ifType.setAttribute("name", "if" + compCounter)
    ifType.dataset.parent = parentIndex;
    ifType.dataset.counter = compCounter;

    const typeDefaultOption = document.createElement("option");
    typeDefaultOption.setAttribute("selected", true)
    typeDefaultOption.appendChild(document.createTextNode("- Choose type -"));
    const typeOption1 = document.createElement("option");
    typeOption1.setAttribute("value", CATEGORY);
    typeOption1.appendChild(document.createTextNode(CATEGORY));
    const typeOption2 = document.createElement("option");
    typeOption2.setAttribute("value", PRODUCT);
    typeOption2.appendChild(document.createTextNode(PRODUCT));
    const typeOption3 = document.createElement("option");
    typeOption3.setAttribute("value", COMP);
    typeOption3.appendChild(document.createTextNode(COMP));

    ifType.appendChild(typeDefaultOption);
    ifType.appendChild(typeOption1);
    ifType.appendChild(typeOption2);
    ifType.appendChild(typeOption3);

    ifType.addEventListener("focus", (elem) => {
        prevSelect[elem.target.id] = elem.target.value
    })

    ifType.addEventListener("change", (elem) => {
        operandTypeChanged(elem.target.value, prevSelect[elem.target.id], elem.target.dataset.counter, elem.target.id);
        prevSelect[elem.target.id] = elem.target.value
    })

    contentDiv.appendChild(ifType);

    // Then
    const thenDiv = document.createElement("div");
    thenDiv.className = "input-group-prepend";
    const thenTextSpan = document.createElement("span");
    thenTextSpan.className = "input-group-text";
    thenTextSpan.append(document.createTextNode("Then"))
    thenDiv.appendChild(thenTextSpan);
    contentDiv.appendChild(thenDiv);

    const thenType = document.createElement("select")
    thenType.setAttribute("id", "then" + compCounter)
    thenType.setAttribute("name", "then" + compCounter)
    thenType.dataset.parent = parentIndex;
    thenType.dataset.counter = compCounter;

    const thenDefaultOption = document.createElement("option");
    thenDefaultOption.setAttribute("selected", true)
    thenDefaultOption.appendChild(document.createTextNode("- Choose type -"));
    allDiscounts.forEach(discount => {
        const thenOption = document.createElement("option");
        thenOption.setAttribute("value", discount.discountId);
        thenOption.appendChild(document.createTextNode(discount.discountString));
        thenType.appendChild(thenOption);
    });

    thenType.addEventListener("focus", (elem) => {
        prevSelect[elem.target.id] = elem.target.value
    })

    thenType.addEventListener("change", (elem) => {
        operandTypeChanged(elem.target.value, prevSelect[elem.target.id], elem.target.dataset.counter, elem.target.id);
        prevSelect[elem.target.id] = elem.target.value
    })

    contentDiv.appendChild(thenType);
    document.getElementById("policy-content").appendChild(ifDiv);

    totalComp++
}

function createProductStatement(compCounter, parentIndex, parentID) {

    const productDiv = document.createElement("div");
    productDiv.className = "policyDiv input-group mb-3";
    productDiv.setAttribute("id", PRODUCT + "Div" + compCounter)

    const labelHeader = document.createElement("label");
    labelHeader.append(document.createTextNode("Product #" + compCounter + " of " + parentID))
    productDiv.appendChild(labelHeader);

    const contentDiv = document.createElement("div");
    contentDiv.className = "input-group mb-3";
    productDiv.appendChild(contentDiv);

    // Create content
    const innerProductDiv = document.createElement("div");
    innerProductDiv.className = "input-group-prepend";
    const productTextSpan = document.createElement("span");
    productTextSpan.className = "input-group-text";
    productTextSpan.append(document.createTextNode("product"))
    innerProductDiv.appendChild(productTextSpan);
    contentDiv.appendChild(innerProductDiv);

    const productinput = document.createElement("input");
    productinput.setAttribute("id", "product" + compCounter);
    productinput.setAttribute("type", "text");
    productinput.dataset.parent = parentIndex;
    productinput.dataset.counter = compCounter;
    contentDiv.appendChild(productinput);

    document.getElementById("policy-content").appendChild(productDiv);

    totalComp++;
}

function createCategoryrStatement(compCounter, parentIndex, parentID) {

    const categoryDiv = document.createElement("div");
    categoryDiv.className = "policyDiv input-group mb-3";
    categoryDiv.setAttribute("id", CATEGORY + "Div" + compCounter)

    const labelHeader = document.createElement("label");
    labelHeader.append(document.createTextNode("Category #" + compCounter + " of " + parentID))
    categoryDiv.appendChild(labelHeader);

    const contentDiv = document.createElement("div");
    contentDiv.className = "input-group mb-3";
    categoryDiv.appendChild(contentDiv);

    // Create content
    const innerCategoryDiv = document.createElement("div");
    innerCategoryDiv.className = "input-group-prepend";
    const categoryTextSpan = document.createElement("span");
    categoryTextSpan.className = "input-group-text";
    categoryTextSpan.append(document.createTextNode("category"))
    innerCategoryDiv.appendChild(categoryTextSpan);
    contentDiv.appendChild(innerCategoryDiv);

    const categoryInput = document.createElement("input");
    categoryInput.setAttribute("id", "category" + compCounter);
    categoryInput.setAttribute("type", "text");
    categoryInput.dataset.parent = parentIndex;
    categoryInput.dataset.counter = compCounter;
    contentDiv.appendChild(categoryInput);

    document.getElementById("policy-content").appendChild(categoryDiv);

    totalComp++;
}

function removeDescendants(type, index) {
    if (type == COMP) {
        const ran1Elem = document.getElementById(type + index + "rand1");
        const ran2Elem = document.getElementById(type + index + "rand2");

        checkAndRemove(ran1Elem.dataset.childType, ran1Elem.dataset.child)
        checkAndRemove(ran2Elem.dataset.childType, ran2Elem.dataset.child)
    } else if (type == PRODUCT || type == CATEGORY) {
        const prodElem = document.getElementById(type + index);
        checkAndRemove(prodElem.dataset.childType, prodElem.dataset.child)
    } else {
        const firstElem = document.getElementById("if" + index);
        const secondElem = document.getElementById("then" + index);
        checkAndRemove(firstElem.dataset.childType, firstElem.dataset.child)
        checkAndRemove(secondElem.dataset.childType, secondElem.dataset.child)
    }

    const elemToDelete = document.getElementById(type + "Div" + index);

    elemToDelete.parentElement.removeChild(elemToDelete);
}

function checkAndRemove(type, index) {
    if (type && index) {
        removeDescendants(type, index)
    }
}