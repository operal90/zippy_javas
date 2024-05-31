package com.macrotel.zippyworld_test.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GoogleDriveConfig {
    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static String getPathToGoogleCredentials() {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, "cred.json");
        return filePath.toString();
    }

    public String uploadFileToDrive(File file) throws GeneralSecurityException, IOException {
        String fileUrl = "";
        try{
            String folderId = "1YrFVOOgsd7075MaHHUQE8uAhSQuJQfkC";
            Drive drive= createDriveService();
            com.google.api.services.drive.model.File fileMetaData =  new  com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));

            String mimeType = getMimeType(file);
            FileContent mediaContent = new FileContent(mimeType, file);
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id").execute();
            fileUrl = "https://drive.google.com/thumbnail?id="+uploadedFile.getId()+"&sz=w1000";
            file.delete();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return fileUrl;
    }
    private String getMimeType(File file) {
        Map<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("doc", "application/msword");
        mimeTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return mimeTypes.getOrDefault(fileExtension, "application/octet-stream");
    }


    private Drive createDriveService() throws GeneralSecurityException, IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(googleCredentials))
                .setApplicationName("Loan Application")
                .build();
    }
}
