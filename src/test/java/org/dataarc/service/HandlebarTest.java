package org.dataarc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class HandlebarTest {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws IOException {
        Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline("{{title}} ({{source}}) {{ data.[0].name }}");
            Map<String,Object> test = new HashMap<>();
            Map<String,Object> test2 = new HashMap<>();
            test2.put("name", "my name is");
            test.put("title", "my title");
            test.put("source", "SEAD");
            List<Object> list = new ArrayList<>();
            list.add(test2);
            test.put("data", list);
            String val = template.apply(test);
            logger.debug(val);

    }
    
}