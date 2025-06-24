package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;


    /**
     * Save uploaded procedure file into db
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Optional: Add metadata
        org.bson.Document metadata = new org.bson.Document();
        metadata.put("contentType", file.getContentType());
        metadata.put("originalName", file.getOriginalFilename());

        return gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metadata
        ).toString(); // returns fileId as string
    }

    /**
     * Download uploaded procedure file
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    public GridFsResource downloadFile(String fileId) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));

        if (file == null) {
            throw new FileNotFoundException("File not found with id: " + fileId);
        }

        return gridFsOperations.getResource(file);
    }


}
