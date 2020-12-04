function setDataChoice(inputId, datalistId, templateId) {
    let input = document.querySelector('#' + inputId);
    let dataList = document.querySelector('#' + datalistId);
    let template = document.querySelector('#' + templateId).content;
    input.addEventListener('keyup', function handler(event) {
        while (dataList.children.length) dataList.removeChild(dataList.firstChild);
        let inputVal = new RegExp(input.value.trim(), 'i');
        let set = Array.prototype.reduce.call(template.cloneNode(true).children, function searchFilter(frag, item, i) {
            if (inputVal.test(item.textContent) && frag.children.length < 6) frag.appendChild(item);
            return frag;
        }, document.createDocumentFragment());
        dataList.appendChild(set);
    });
}