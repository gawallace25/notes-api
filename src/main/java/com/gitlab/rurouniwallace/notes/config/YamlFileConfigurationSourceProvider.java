package com.gitlab.rurouniwallace.notes.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.dropwizard.configuration.FileConfigurationSourceProvider;

public class YamlFileConfigurationSourceProvider extends FileConfigurationSourceProvider {

	public InputStream open(final String path) throws IOException {
		final InputStream rawYamlStream = super.open(path);
		
		final Yaml yaml = new Yaml();
		final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		final String yamlString = mapper.writeValueAsString(yaml.load(rawYamlStream));
		
		final InputStream yamlStream = new ByteArrayInputStream(yamlString.getBytes("UTF-8"));
	
		return yamlStream;
	}
}
