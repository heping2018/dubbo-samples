package org.apache.dubbo.samples.provider;

import org.apache.dubbo.samples.api.TestService;

/**
 * @author 86187
 * @description <TODO description class purpose>
 * @create 2023/2/19 15:10
 **/
public class TestServiceImpl implements TestService {
    @Override
    public String sayTest(String name) {
        return "Test";
    }
}
