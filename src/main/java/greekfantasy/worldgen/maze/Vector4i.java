package greekfantasy.worldgen.maze;

import java.util.Objects;

/**
 * An ordered set of 4 objects. This class is immutable but the objects it contains may not be.
 * @param <T> any object
 */
public class Vector4i<T> {
	public final T a;
	public final T b;
	public final T c;
	public final T d;
	
	public Vector4i(T a, T b, T c, T d) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vector4i<?> vector4i = (Vector4i<?>) o;
		return Objects.equals(a, vector4i.a) && Objects.equals(b, vector4i.b) && Objects.equals(c, vector4i.c) && Objects.equals(d, vector4i.d);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c, d);
	}
}
