package com.carrental.controller.web.profile;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 12 * 1024 * 1024
)
public class ProfileKycUpdateServlet extends BaseAuthentication {

    private static final String UPLOAD_DIR = "/assets/uploads/kyc";

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute(IConstant.SESSION_USER);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String identityCardNumber = request.getParameter("identityCardNumber");
        if (ValidationUtil.isBlank(identityCardNumber)) {
            response.sendRedirect(request.getContextPath() + "/profile?error=identity_required");
            return;
        }

        Part frontPart = request.getPart("licenseImageFront");
        Part backPart = request.getPart("licenseImageBack");

        UserDAO dao = new UserDAO();
        User current = dao.getUserById(sessionUser.getUserID());

        String frontPath = current == null ? null : current.getLicenseImageFront();
        String backPath = current == null ? null : current.getLicenseImageBack();

        if (frontPart != null && frontPart.getSize() > 0) {
            if (!ValidationUtil.isImageContentType(frontPart.getContentType())) {
                response.sendRedirect(request.getContextPath() + "/profile?error=invalid_front_image_type");
                return;
            }
            frontPath = saveFile(request, frontPart, "front", sessionUser.getUserID());
        }

        if (backPart != null && backPart.getSize() > 0) {
            if (!ValidationUtil.isImageContentType(backPart.getContentType())) {
                response.sendRedirect(request.getContextPath() + "/profile?error=invalid_back_image_type");
                return;
            }
            backPath = saveFile(request, backPart, "back", sessionUser.getUserID());
        }

        boolean ok = dao.updateProfileKyc(
                sessionUser.getUserID(),
                identityCardNumber.trim(),
                frontPath,
                backPath
        );

        if (!ok) {
            response.sendRedirect(request.getContextPath() + "/profile?error=kyc_update_failed");
            return;
        }

        User latest = dao.getUserById(sessionUser.getUserID());
        session.setAttribute(IConstant.SESSION_USER, latest);
        response.sendRedirect(request.getContextPath() + "/profile?kycUpdated=1");
    }

    private String saveFile(HttpServletRequest request, Part part, String side, int userID) throws IOException {
        String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String ext = ValidationUtil.safeFileExtension(original);
        String fileName = "u" + userID + "_" + side + "_" + UUID.randomUUID().toString().replace("-", "") + ext;

        String realUploadDir = request.getServletContext().getRealPath(UPLOAD_DIR);
        File dir = new File(realUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String absolutePath = dir.getAbsolutePath() + File.separator + fileName;
        part.write(absolutePath);

        return UPLOAD_DIR + "/" + fileName;
    }
}
