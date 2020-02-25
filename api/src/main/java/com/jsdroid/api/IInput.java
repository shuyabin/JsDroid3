package com.jsdroid.api;

import com.jsdroid.ipc.annotations.Timeout;
import com.jsdroid.ipc.data.IpcService;

public interface IInput extends IpcService {
    @Timeout(2000)
    void clear(int before, int after) throws InterruptedException;

    @Timeout(2000)
    void input(String text) throws InterruptedException;

    @Timeout(1000)
    void performAction(int action) throws InterruptedException;

    @Timeout(1000)
    void inputGo() throws InterruptedException;

    @Timeout(1000)
    void inputDone() throws InterruptedException;

    @Timeout(1000)
    void inputNext() throws InterruptedException;

    @Timeout(1000)
    void inputSearch() throws InterruptedException;

    @Timeout(1000)
    void inputSend() throws InterruptedException;

    @Timeout(1000)
    void inputUnspecified() throws InterruptedException;
}
