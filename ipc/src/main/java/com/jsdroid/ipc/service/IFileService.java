package com.jsdroid.ipc.service;


import com.jsdroid.ipc.data.IpcService;

public interface IFileService extends IpcService {
    byte[] readFile(String filePath);

    void saveFile(String filePath, byte[] fileData);
}
