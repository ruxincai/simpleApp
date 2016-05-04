package shu.example;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shu.example.handlers.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class EndPoint {

private String allowed;
private final Map<String, Handler> handlers = new HashMap<>();
private final Map<String, EndPoint> children = new HashMap<>();
private EndPoint defaultChild;

@Nullable
public Handler getHandler(@NotNull String method) {
	return handlers.get(method);
}

@Nullable
public EndPoint getChild(@NotNull String name,
		@NotNull List<String> ids) {
	EndPoint child = children.get(name);
	if (child == null && defaultChild != null) {
		child = defaultChild;
		ids.add(name);
	}
	return child;
}

@NotNull
public String getAllowed() {
	if (allowed == null) {
		allowed = handlers.keySet().stream().collect(Collectors.joining(", "));
	}
	return allowed;
}

public void addHandler(@NotNull String method, @NotNull Handler handler) {
	handlers.put(method, handler);
}

public void addChild(@NotNull String name, @NotNull EndPoint endPoint) {
	children.put(name, endPoint);
}

public void setDefaultChild(@NotNull EndPoint defaultChild) {
	this.defaultChild = defaultChild;
}

}
