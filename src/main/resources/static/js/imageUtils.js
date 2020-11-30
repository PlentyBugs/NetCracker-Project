$(() => {
    $("#upload-image-modal-container").append(
        "<div class='modal fade' id='upload-image-modal' tabindex='-1' role='dialog' aria-hidden='true'>" +
        "        <div class='modal-dialog' role='document'>" +
        "            <div class='modal-content bg-dark'>" +
        "                <div class='modal-header text-center d-block'>" +
        "                    <h5 class='modal-title'>Upload Image</h5>" +
        "                </div>" +
        "                <div class='modal-body' id='upload-image-body'>" +
        "                    <div class='custom-file'>" +
        "                        <input type='file' class='custom-file-input' id='upload-image-input' name='title' accept='image/png, image/jpeg'/>" +
        "                        <label class='custom-file-label file-label' for='customFile'>Upload image</label>" +
        "                    </div>" +
        "                    <div class='alert-danger d-none' id='upload-image-alert'>Image is too big</div>" +
        "                    <div class='w-100' id='upload-image-preview'>" +
        "                       <div class='mw-100' id='upload-image-result'></div>" +
        "                    </div>" +
        "                </div>" +
        "            </div>" +
        "        </div>" +
        "    </div>"
    );

    let imageInput = $("#upload-image-input"),
        imageAlert = $("#upload-image-alert"),
        imagePreview = $("#upload-image-preview"),
        result = $("#upload-image-result"),
        options = $('.options'),
        save = $('.save'),
        regexp = /(https?:\/\/.+?\/.+?\/\d+)\/?.*/,
        token = $('#_csrf').attr('content'),
        header = $('#_csrf_header').attr('content'),
        url = document.URL.match(regexp)[1];

    imageInput.change((e) => {
        imageAlert.addClass("d-none");
        $("#upload-image-button").remove();
        const reader = new FileReader();
        reader.onload = (e) => {
            if (e.total > 20971520) {
                imageAlert.removeClass("d-none");
            } else if(e.target.result) {
                let x, y, width, height;
                let uploadImageButton = $("<button type='button' class='btn btn-warning btn-block' id='upload-image-button'>Upload</button>");

                uploadImageButton.click(() => {
                    let data = new FormData();
                    data.append("avatar", imageInput.prop("files")[0]);
                    $.ajax({
                        type: 'PUT',
                        url: url + "/image",
                        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
                        data: data,
                        enctype: 'multipart/form-data',
                        cache: false,
                        async: false,
                        processData: false,
                        contentType: false,
                    });
                    window.location = window.location.href;
                });
                imagePreview.after(uploadImageButton);
                let img = $("<img class='mw-100' id='uploaded-image-cropper' src='" + e.target.result + "' style='max-width: 100%'>");
                result.empty();
                result.append(img);
                save.removeClass('d-none');
                options.removeClass('d-none');
                let cropper  = new Croppr("#uploaded-image-cropper", {
                    returnMode: "real",
                    maxSize: [100, 100, '%'],
                    minSize: [10, 10, '%'],
                    onCropEnd: (image) => {
                        uploadImageButton.unbind("click");
                        uploadImageButton.click(() => {
                            let data = new FormData();
                            data.append("avatar", imageInput.prop("files")[0]);
                            data.append("x", image.x);
                            data.append("y", image.y);
                            data.append("width", image.width);
                            data.append("height", image.height);
                            $.ajax({
                                type: 'PUT',
                                url: url + "/image",
                                beforeSend: (xhr) => xhr.setRequestHeader(header, token),
                                data: data,
                                enctype: 'multipart/form-data',
                                cache: false,
                                async: false,
                                processData: false,
                                contentType: false,
                            });
                            window.location = window.location.href;
                        });
                    }
                });
            }
        };
        reader.readAsDataURL(e.target.files[0]);
    });
});